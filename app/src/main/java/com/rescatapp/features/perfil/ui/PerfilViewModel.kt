package com.rescatapp.features.perfil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.domain.seleccionarPedidosPorRol
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.auth.domain.model.SesionUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PerfilViewModel @Inject constructor(
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoUseCase,
    private val repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    val uiState: StateFlow<PerfilUiState> = combine(
        repositorioPedidos.pedidos,
        UsuarioDemo.rol,
        repositorioAutenticacion.sesionActual
    ) { pedidos, rol, sesion ->
        crearEstado(pedidos, sesion?.rol ?: rol, sesion)
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = crearEstado(emptyList(), UsuarioDemo.rol.value, null)
    )

    fun cerrarSesion() {
        viewModelScope.launch { repositorioAutenticacion.cerrarSesion() }
    }

    private fun crearEstado(
        pedidos: List<Pedido>,
        rol: RolUsuario,
        sesion: SesionUsuario?
    ): PerfilUiState {
        val esNegocio = rol == RolUsuario.NEGOCIO
        return PerfilUiState(
            impacto = calcularImpacto(
                seleccionarPedidosPorRol(pedidos, rol, sesion?.nombreNegocio)
            ),
            nombre = sesion?.let { it.nombreNegocio ?: it.nombre }
                ?: if (esNegocio) UsuarioDemo.NEGOCIO_DEMO else UsuarioDemo.NOMBRE,
            correo = sesion?.correo
                ?: if (esNegocio) UsuarioDemo.CORREO_NEGOCIO else UsuarioDemo.CORREO,
            esNegocio = esNegocio,
            sesion = sesion
        )
    }
}
