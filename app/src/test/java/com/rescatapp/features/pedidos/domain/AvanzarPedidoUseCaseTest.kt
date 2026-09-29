package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AvanzarPedidoUseCaseTest {
    private val repositorioPedidos = RepositorioPedidos()
    private val avanzarPedido = AvanzarPedidoUseCase(repositorioPedidos)

    @Test
    fun avanzaUnPedidoPreparandoAListo() {
        val resultado = avanzarPedido(4)

        assertTrue(resultado is ResultadoOperacion.Exito)
        assertEquals(EstadoPedido.LISTO, repositorioPedidos.buscarPorId(4)?.estado)
    }

    @Test
    fun confirmarRetiroDejaElPedidoRecogido() {
        avanzarPedido(5)

        assertEquals(EstadoPedido.RECOGIDO, repositorioPedidos.buscarPorId(5)?.estado)
    }

    @Test
    fun noAvanzaUnPedidoCancelado() {
        val resultado = avanzarPedido(1)

        assertEquals(ResultadoOperacion.Error("El pedido ya está Cancelado"), resultado)
        assertEquals(EstadoPedido.CANCELADO, repositorioPedidos.buscarPorId(1)?.estado)
    }

    @Test
    fun informaCuandoElPedidoNoExiste() {
        assertEquals(ResultadoOperacion.Error("Pedido no encontrado"), avanzarPedido(99))
    }
}
