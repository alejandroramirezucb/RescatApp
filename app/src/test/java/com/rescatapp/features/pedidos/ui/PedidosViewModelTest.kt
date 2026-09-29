package com.rescatapp.features.pedidos.ui

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.mock.pedidosDeEjemplo
import com.rescatapp.features.pedidos.domain.AvanzarPedidoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PedidosViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var repositorioPedidos: RepositorioPedidos
    private lateinit var avanzarPedidoUseCase: AvanzarPedidoUseCase
    private lateinit var viewModel: PedidosViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repositorioPedidos = RepositorioPedidos()
        pedidosDeEjemplo.forEach { repositorioPedidos.agregar(it) }
        avanzarPedidoUseCase = AvanzarPedidoUseCase(repositorioPedidos)
        viewModel = PedidosViewModel(repositorioPedidos, avanzarPedidoUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `estado inicial tiene la pestana ACTIVOS y listas ordenadas por ID descendente`() =
        runTest {
            val estado = viewModel.uiState.first()

            assertEquals(PestanaPedidos.ACTIVOS, estado.pestana)
            assertTrue(estado.activos.isNotEmpty())
            assertTrue(estado.historial.isNotEmpty())

            // Verificar orden descendente por ID
            val idsActivos = estado.activos.map { it.id }
            assertEquals(idsActivos.sortedDescending(), idsActivos)

            val idsHistorial = estado.historial.map { it.id }
            assertEquals(idsHistorial.sortedDescending(), idsHistorial)
        }

    @Test
    fun `cambiarPestana actualiza la pestana seleccionada`() = runTest {
        viewModel.cambiarPestana(PestanaPedidos.HISTORIAL)

        val estado = viewModel.uiState.first { it.pestana == PestanaPedidos.HISTORIAL }
        assertEquals(PestanaPedidos.HISTORIAL, estado.pestana)
    }

    @Test
    fun `avanzarPedido cambia el estado del pedido y actualiza la lista`() = runTest {
        val primerPedido = viewModel.uiState.first().activos.first()
        val estadoInicial = primerPedido.estado

        viewModel.avanzarPedido(primerPedido.id)

        val estadoNuevo = viewModel.uiState.first()
        val pedidoActualizado = (estadoNuevo.activos + estadoNuevo.historial).find {
            it.id ==
                primerPedido.id
        }

        assertEquals(estadoInicial.siguiente(), pedidoActualizado?.estado)
    }
}
