package com.rescatapp.features.perfil.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.domain.seleccionarPedidosPorRol
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.util.suscripcionPantalla
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PerfilViewModel @Inject constructor(
    repositorioPedidos: RepositorioPedidos,
    private val calcularImpacto: CalcularImpactoUseCase
) : ViewModel() {

    val uiState: StateFlow<PerfilUiState> = combine(
        repositorioPedidos.pedidos,
        UsuarioDemo.rol
    ) { pedidos, rol ->
        crearEstado(seleccionarPedidosPorRol(pedidos, rol), rol)
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = crearEstado(repositorioPedidos.pedidos.value, UsuarioDemo.rol.value)
    )

    private fun crearEstado(
        pedidos: List<Pedido>,
        rol: RolUsuario = UsuarioDemo.rol.value
    ): PerfilUiState {
        val esNegocio = rol == RolUsuario.NEGOCIO
        val nombre = if (esNegocio) UsuarioDemo.NEGOCIO_DEMO else UsuarioDemo.NOMBRE
        val correo = if (esNegocio) UsuarioDemo.CORREO_NEGOCIO else UsuarioDemo.CORREO
        val inicial = if (esNegocio) UsuarioDemo.NEGOCIO_DEMO.take(1) else UsuarioDemo.inicial
        return PerfilUiState(
            impacto = calcularImpacto(pedidos),
            nombre = nombre,
            correo = correo,
            inicial = inicial,
            esNegocio = esNegocio
        )
    }
}
