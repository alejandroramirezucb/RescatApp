package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.suscripcionPantalla
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InicioNegocioViewModel @Inject constructor(
    repositorioOfertas: RepositorioOfertas,
    repositorioPedidos: RepositorioPedidos
) : ViewModel() {

    val uiState: StateFlow<InicioNegocioUiState> =
        combine(
            repositorioOfertas.ofertas,
            repositorioPedidos.pedidos
        ) { ofertas, pedidos ->
            val negocio = UsuarioDemo.NEGOCIO_DEMO
            val ofertasNegocio = ofertas.filter { it.comercio == negocio }
            val pedidosNegocio = pedidos.filter { it.comercio == negocio }
            val pedidosVigentes = pedidosNegocio.filter {
                it.estado != EstadoPedido.CANCELADO
            }
            val ventasTotal = pedidosVigentes.sumOf { it.precioPagado }
            val rescatadosTotal = pedidosNegocio.count {
                it.estado == EstadoPedido.RECOGIDO || it.estado == EstadoPedido.LISTO
            }

            InicioNegocioUiState(
                nombreNegocio = negocio,
                ventasHoy = ventasTotal.formatearDinero(),
                cantidadPedidos = pedidosNegocio.size,
                ofertasActivas = ofertasNegocio.count { !it.estaAgotada },
                rescatados = rescatadosTotal,
                pedidosRecientes = pedidosNegocio.sortedByDescending { it.id }
            )
        }.stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = InicioNegocioUiState()
        )
}
