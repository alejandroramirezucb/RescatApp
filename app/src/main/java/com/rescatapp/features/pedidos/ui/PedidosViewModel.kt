package com.rescatapp.features.pedidos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.pedidos.domain.AvanzarPedidoUseCase
import com.rescatapp.features.pedidos.domain.CancelarPedidoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class PedidosViewModel @Inject constructor(
    private val repositorioPedidos: RepositorioPedidos,
    private val avanzarPedidoUseCase: AvanzarPedidoUseCase,
    private val cancelarPedidoUseCase: CancelarPedidoUseCase
) : ViewModel() {

    private val pestanaSeleccionada = MutableStateFlow(PestanaPedidos.ACTIVOS)
    private val mensajeError = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PedidosUiState> = combine(
        repositorioPedidos.pedidos,
        pestanaSeleccionada,
        mensajeError
    ) { listaPedidos, pestana, mensaje ->
        val activos = listaPedidos
            .filter { it.estaActivo }
            .sortedByDescending { it.id }

        val historial = listaPedidos
            .filter { !it.estaActivo }
            .sortedByDescending { it.id }

        PedidosUiState(
            pestana = pestana,
            activos = activos,
            historial = historial,
            mensaje = mensaje
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PedidosUiState()
    )

    fun cambiarPestana(pestana: PestanaPedidos) {
        pestanaSeleccionada.value = pestana
    }

    fun avanzarPedido(pedidoId: Int) {
        when (val resultado = avanzarPedidoUseCase(pedidoId)) {
            is ResultadoOperacion.Exito<*> -> {
                mensajeError.value = null
            }

            is ResultadoOperacion.Error -> {
                mensajeError.value = resultado.mensaje
            }
        }
    }

    fun cancelarPedido(pedidoId: Int) {
        when (val resultado = cancelarPedidoUseCase(pedidoId)) {
            is ResultadoOperacion.Exito<*> -> {
                mensajeError.value = null
            }

            is ResultadoOperacion.Error -> {
                mensajeError.value = resultado.mensaje
            }
        }
    }

    fun limpiarMensaje() {
        mensajeError.value = null
    }
}
