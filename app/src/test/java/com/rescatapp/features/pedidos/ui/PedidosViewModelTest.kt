package com.rescatapp.features.pedidos.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.features.pedidos.domain.AvanzarPedidoUseCase
import com.rescatapp.features.pedidos.domain.CancelarPedidoUseCase
import com.rescatapp.features.pedidos.domain.ConstruirProgresoPedidoUseCase
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PedidosViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioPedidos = RepositorioPedidos()

    @Test
    fun separaActivosEHistorialDelMasRecienteAlMasAntiguo() {
        val estado = crearViewModel().uiState.value

        assertEquals(listOf(5, 4), estado.activos.map { it.pedido.id })
        assertEquals(listOf(3, 2, 1), estado.historial.map { it.id })
    }

    @Test
    fun confirmarRetiroMueveElPedidoAlHistorial() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.avanzar(5)

        assertEquals(listOf(4), viewModel.uiState.value.activos.map { it.pedido.id })
        assertEquals(5, viewModel.uiState.value.historial.first().id)
    }

    @Test
    fun unaAccionInvalidaMuestraElMotivoHastaLimpiarlo() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.cancelar(4)
        assertEquals("Solo puedes cancelar un pedido reservado", viewModel.uiState.value.mensaje)

        viewModel.limpiarMensaje()
        assertNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun cambiarDePestanaActualizaElEstado() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.seleccionarPestana(PestanaPedidos.HISTORIAL)

        assertEquals(PestanaPedidos.HISTORIAL, viewModel.uiState.value.pestana)
    }

    private fun crearViewModel() = PedidosViewModel(
        repositorioPedidos = repositorioPedidos,
        avanzarPedido = AvanzarPedidoUseCase(repositorioPedidos),
        cancelarPedido = CancelarPedidoUseCase(repositorioPedidos, RepositorioOfertas()),
        construirProgreso = ConstruirProgresoPedidoUseCase()
    )
}
