package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservarOfertaUseCaseTest {
    @Test
    fun reservaCroissantsCopiaDatosYActualizaDisponibilidad() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val resultado = ReservarOfertaUseCase(ofertas, pedidos)(13)

        assertTrue(resultado is ResultadoOperacion.Exito<*>)
        val pedido = resultado.valorExitoso
        assertNotNull(pedido)
        assertEquals(EstadoPedido.RESERVADO, pedido?.estado)
        assertEquals("Pack Croissants", pedido?.nombreOferta)
        assertEquals(15.0, pedido?.precioPagado ?: 0.0, 0.0)
        assertEquals(15.0, pedido?.ahorro ?: 0.0, 0.0)
        assertEquals(0.6, pedido?.pesoKg ?: 0.0, 0.0)
        assertEquals(2, ofertas.obtenerActualPorId(13)?.cantidadDisponible)
        assertEquals(pedido, pedidos.pedidos.value.first())
        assertTrue(pedido?.estaActivo == true)
    }

    @Test
    fun noReservaUnaOfertaAgotadaNiModificaLosRepositorios() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val oferta = ofertas.obtenerActualPorId(13)!!
        ofertas.actualizar(oferta.copy(cantidadDisponible = 0))

        val resultado = ReservarOfertaUseCase(ofertas, pedidos)(13)

        assertEquals(ResultadoOperacion.Error("Esta oferta está agotada"), resultado)
        assertEquals(0, ofertas.obtenerActualPorId(13)?.cantidadDisponible)
        assertEquals(5, pedidos.pedidos.value.size)
    }

    @Test
    fun informaSiLaOfertaNoExiste() {
        val pedidos = RepositorioPedidos()
        val resultado = ReservarOfertaUseCase(RepositorioOfertas(), pedidos)(999)

        assertEquals(ResultadoOperacion.Error("Oferta no encontrada"), resultado)
        assertEquals(5, pedidos.pedidos.value.size)
    }

    @Test
    fun reservaLaUltimaUnidadYDejaLaOfertaAgotada() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val oferta = ofertas.obtenerActualPorId(13)!!
        ofertas.actualizar(oferta.copy(cantidadDisponible = 1))

        val resultado = ReservarOfertaUseCase(ofertas, pedidos)(13)

        assertTrue(resultado is ResultadoOperacion.Exito<*>)
        assertEquals(0, ofertas.obtenerActualPorId(13)?.cantidadDisponible)
        assertEquals(6, pedidos.pedidos.value.size)
    }
}
