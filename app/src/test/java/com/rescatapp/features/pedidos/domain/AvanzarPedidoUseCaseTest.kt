package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AvanzarPedidoUseCaseTest {
    private val fakePedidoDao = FakePedidoDao()
    private val repositorioPedidos = RepositorioPedidos(fakePedidoDao)
    private val avanzarPedido = AvanzarPedidoUseCase(repositorioPedidos)

    @Test
    fun avanzaUnPedidoPreparandoAListo() = runTest {
        val resultado = avanzarPedido(4)

        assertTrue(resultado is ResultadoOperacion.Exito)
        assertEquals(EstadoPedido.LISTO, repositorioPedidos.buscarPorId(4)?.estado)
    }

    @Test
    fun confirmarRetiroDejaElPedidoRecogido() = runTest {
        avanzarPedido(5)

        assertEquals(EstadoPedido.RECOGIDO, repositorioPedidos.buscarPorId(5)?.estado)
    }

    @Test
    fun noAvanzaUnPedidoCancelado() = runTest {
        val resultado = avanzarPedido(1)

        assertEquals(ResultadoOperacion.Error("El pedido ya está Cancelado"), resultado)
        assertEquals(EstadoPedido.CANCELADO, repositorioPedidos.buscarPorId(1)?.estado)
    }

    @Test
    fun informaCuandoElPedidoNoExiste() = runTest {
        assertEquals(ResultadoOperacion.Error("Pedido no encontrado"), avanzarPedido(99))
    }
}
