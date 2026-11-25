package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.hoyEnBolivia
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.inicio.domain.CalcularVentasSemanaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class InicioNegocioViewModel @Inject constructor(
    repositorioOfertas: RepositorioOfertas,
    repositorioPedidos: RepositorioPedidos,
    calcularVentasSemana: CalcularVentasSemanaUseCase,
    repositorioNotificaciones: RepositorioNotificaciones,
    repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {

    val uiState: StateFlow<InicioNegocioUiState> =
        combine(
            repositorioOfertas.propias,
            repositorioPedidos.pedidos,
            repositorioNotificaciones.contarNoLeidas(RolUsuario.NEGOCIO),
            repositorioAutenticacion.sesionActual
        ) { ofertas, pedidosNegocio, noLeidas, sesion ->
            val pedidosVigentes = pedidosNegocio.filter {
                it.estado == EstadoPedido.RECOGIDO &&
                    it.fecha == hoyEnBolivia()
            }
            val ventasTotal = pedidosVigentes.sumOf { it.precioPagado }
            val rescatadosTotal = pedidosNegocio.count {
                it.estado == EstadoPedido.RECOGIDO
            }

            InicioNegocioUiState(
                nombreNegocio = sesion?.nombreNegocio.orEmpty(),
                ventasHoy = ventasTotal.formatearDinero(),
                cantidadPedidos = pedidosNegocio.size,
                ofertasActivas = ofertas.count {
                    !it.estaAgotada &&
                        it.estadoPublicacion == "ACTIVA"
                },
                rescatados = rescatadosTotal,
                pedidosRecientes = pedidosNegocio.sortedByDescending { it.id },
                ventasSemana = calcularVentasSemana(pedidosNegocio),
                notificacionesNoLeidas = noLeidas
            )
        }.stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = InicioNegocioUiState()
        )
}
