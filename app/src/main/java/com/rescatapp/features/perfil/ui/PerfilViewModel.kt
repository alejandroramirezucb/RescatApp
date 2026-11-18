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
        repositorioAutenticacion.sesionActual
    ) { pedidos, sesion ->
        crearEstado(pedidos, sesion)
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = crearEstado(emptyList(), null)
    )

    fun cerrarSesion() {
        viewModelScope.launch { repositorioAutenticacion.cerrarSesion() }
    }

    private fun crearEstado(pedidos: List<Pedido>, sesion: SesionUsuario?): PerfilUiState {
        val rol = sesion?.rol ?: RolUsuario.CLIENTE
        return PerfilUiState(
            impacto = calcularImpacto(
                seleccionarPedidosPorRol(pedidos, rol, sesion?.nombreNegocio)
            ),
            nombre = sesion?.let { it.nombreNegocio ?: it.nombre } ?: UsuarioDemo.NOMBRE,
            correo = sesion?.correo ?: UsuarioDemo.CORREO,
            esNegocio = rol == RolUsuario.NEGOCIO,
            sesion = sesion
        )
    }
}
