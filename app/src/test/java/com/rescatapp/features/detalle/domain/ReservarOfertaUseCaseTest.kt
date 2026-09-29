package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservarOfertaUseCaseTest {
    @Test
    fun reservaUnaOfertaDisponible() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val resultado = ReservarOfertaUseCase(ofertas, pedidos)(13)

        assertTrue(resultado is ResultadoOperacion.Exito)
        val pedido = (resultado as ResultadoOperacion.Exito).valor as Pedido
        assertEquals(EstadoPedido.RESERVADO, pedido.estado)
        assertEquals("Pack Croissants", pedido.nombreOferta)
        assertEquals(2, ofertas.obtenerActualPorId(13)?.cantidadDisponible)
        assertEquals(pedido, pedidos.pedidos.value.first())
    }

    @Test
    fun noReservaUnaOfertaAgotada() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val oferta = ofertas.obtenerActualPorId(13)!!
        val cantidadInicial = pedidos.pedidos.value.size
        ofertas.actualizar(oferta.copy(cantidadDisponible = 0))

        val resultado = ReservarOfertaUseCase(ofertas, pedidos)(13)

        assertEquals(ResultadoOperacion.Error("Esta oferta está agotada"), resultado)
        assertEquals(cantidadInicial, pedidos.pedidos.value.size)
    }

    @Test
    fun informaCuandoLaOfertaNoExiste() {
        val resultado = ReservarOfertaUseCase(RepositorioOfertas(), RepositorioPedidos())(999)

        assertEquals(ResultadoOperacion.Error("Oferta no encontrada"), resultado)
    }
}
