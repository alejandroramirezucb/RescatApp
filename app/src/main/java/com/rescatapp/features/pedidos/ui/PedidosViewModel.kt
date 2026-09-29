package com.rescatapp.features.pedidos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.pedidos.domain.AvanzarPedidoUseCase
import com.rescatapp.features.pedidos.domain.CancelarPedidoUseCase
import com.rescatapp.features.pedidos.domain.ConstruirProgresoPedidoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PedidosViewModel @Inject constructor(
    repositorioPedidos: RepositorioPedidos,
    private val avanzarPedido: AvanzarPedidoUseCase,
    private val cancelarPedido: CancelarPedidoUseCase,
    private val construirProgreso: ConstruirProgresoPedidoUseCase
) : ViewModel() {
    private val pestana = MutableStateFlow(PestanaPedidos.ACTIVOS)
    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PedidosUiState> =
        combine(repositorioPedidos.pedidos, pestana, mensaje, ::crearEstado).stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = crearEstado(repositorioPedidos.pedidos.value, pestana.value, null)
        )

    fun seleccionarPestana(pestanaElegida: PestanaPedidos) {
        pestana.value = pestanaElegida
    }

    fun avanzar(pedidoId: Int) = mostrarErrorSiFalla(avanzarPedido(pedidoId))

    fun cancelar(pedidoId: Int) = mostrarErrorSiFalla(cancelarPedido(pedidoId))

    fun limpiarMensaje() {
        mensaje.value = null
    }

    private fun mostrarErrorSiFalla(resultado: ResultadoOperacion) {
        mensaje.value = (resultado as? ResultadoOperacion.Error)?.mensaje
    }

    private fun crearEstado(
        pedidos: List<Pedido>,
        pestanaActual: PestanaPedidos,
        mensajeActual: String?
    ): PedidosUiState {
        val masRecientesPrimero = pedidos.sortedByDescending { it.id }
        return PedidosUiState(
            pestana = pestanaActual,
            activos = masRecientesPrimero
                .filter { it.estaActivo }
                .map { PedidoConProgreso(it, construirProgreso(it.estado)) },
            historial = masRecientesPrimero.filterNot { it.estaActivo },
            mensaje = mensajeActual
        )
    }
}
