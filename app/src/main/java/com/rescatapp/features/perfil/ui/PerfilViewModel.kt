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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class PerfilViewModel @Inject constructor(
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoUseCase,
    private val repositorioAutenticacion: RepositorioAutenticacion? = null
) : ViewModel() {

    private val flujoSesion: Flow<SesionUsuario?> =
        repositorioAutenticacion?.sesionActual ?: flowOf(null)

    val uiState: StateFlow<PerfilUiState> = combine(
        repositorioPedidos.pedidos,
        UsuarioDemo.rol,
        flujoSesion
    ) { pedidos, rol, sesion ->
        val rolEfectivo = sesion?.rol ?: rol
        val pedidosFiltrados = seleccionarPedidosPorRol(pedidos, rolEfectivo, sesion?.nombreNegocio)
        crearEstado(pedidosFiltrados, rolEfectivo, sesion)
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = crearEstado(emptyList(), UsuarioDemo.rol.value, null)
    )

    fun cerrarSesion() {
        viewModelScope.launch {
            repositorioAutenticacion?.cerrarSesion()
        }
    }

    private fun crearEstado(
        pedidos: List<Pedido>,
        rol: RolUsuario = UsuarioDemo.rol.value,
        sesion: SesionUsuario? = null
    ): PerfilUiState {
        val esNegocio = (sesion?.rol ?: rol) == RolUsuario.NEGOCIO
        val nombre = sesion?.let {
            if (it.esNegocio && !it.nombreNegocio.isNullOrBlank()) it.nombreNegocio else it.nombre
        } ?: if (esNegocio) UsuarioDemo.NEGOCIO_DEMO else UsuarioDemo.NOMBRE

        val correo =
            sesion?.correo ?: if (esNegocio) UsuarioDemo.CORREO_NEGOCIO else UsuarioDemo.CORREO
        val inicial =
            sesion?.inicial
                ?: if (esNegocio) UsuarioDemo.NEGOCIO_DEMO.take(1) else UsuarioDemo.inicial

        return PerfilUiState(
            impacto = calcularImpacto(pedidos),
            nombre = nombre,
            correo = correo,
            inicial = inicial,
            esNegocio = esNegocio,
            sesion = sesion
        )
    }
}
