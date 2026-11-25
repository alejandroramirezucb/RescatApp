package com.rescatapp.features.pedidos.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearAutenticacion
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.crearRepositorioPedidos
import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.EstadoPagoSimulado
import com.rescatapp.features.pedidos.domain.AvanzarPedidoUseCase
import com.rescatapp.features.pedidos.domain.CancelarPedidoUseCase
import com.rescatapp.features.pedidos.domain.ConstruirProgresoPedidoUseCase
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PedidosViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val api = SupabaseApiFalsa(pedidos = pedidosDeEjemplo)
    private val repositorioPedidos = crearRepositorioPedidos(api = api)

    @Test
    fun pulsarConfirmarDosVecesSoloEnviaUnaCancelacion() = runTest {
        repositorioPedidos.reservar(13)
        val id = repositorioPedidos.pedidos.first().first().id
        val espera = CompletableDeferred<Unit>()
        api.esperaCancelacion = espera
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.cancelar(id)
        viewModel.cancelar(id)
        assertEquals(1, api.cancelacionesSolicitadas)
        espera.complete(Unit)

        assertEquals(
            EstadoPagoSimulado.ANULADO,
            viewModel.uiState.value.historial.first().estadoPagoSimulado
        )
    }

    @Test
    fun cancelarMueveAlHistorialYConfirmaLaAnulacionDelPago() = runTest {
        repositorioPedidos.reservar(13)
        val id = repositorioPedidos.pedidos.first().first().id
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.cancelar(id)

        assertEquals(
            EstadoPagoSimulado.ANULADO,
            viewModel.uiState.value.historial.first().estadoPagoSimulado
        )
        assertEquals(
            "Pedido cancelado. El pago simulado fue anulado.",
            viewModel.uiState.value.mensaje
        )
    }

    @Test
    fun separaActivosEHistorialDelMasRecienteAlMasAntiguo() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)
        val estado = viewModel.uiState.value

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
        repositorioAutenticacion = crearAutenticacion(),
        avanzarPedido = AvanzarPedidoUseCase(repositorioPedidos),
        cancelarPedido = CancelarPedidoUseCase(
            repositorioPedidos,
            crearRepositorioOfertas(api = api)
        ),
        construirProgreso = ConstruirProgresoPedidoUseCase()
    )
}
