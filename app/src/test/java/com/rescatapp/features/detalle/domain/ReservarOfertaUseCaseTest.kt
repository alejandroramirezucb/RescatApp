package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservarOfertaUseCaseTest {
    private val repositorioOfertas = RepositorioOfertas()
    private val repositorioPedidos = RepositorioPedidos()
    private val reservarOferta = ReservarOfertaUseCase(repositorioOfertas, repositorioPedidos)

    @Test
    fun reservarDescuentaUnaUnidadYCreaUnPedidoReservado() {
        val resultado = reservarOferta(13)

        assertTrue(resultado is ResultadoOperacion.Exito)
        assertEquals(2, repositorioOfertas.buscarPorId(13)?.cantidadDisponible)
        val pedidoNuevo = repositorioPedidos.pedidos.value.first()
        assertEquals("Pack Croissants", pedidoNuevo.nombreOferta)
        assertEquals(EstadoPedido.RESERVADO, pedidoNuevo.estado)
        assertEquals(6, pedidoNuevo.id)
    }

    @Test
    fun noReservaUnaOfertaAgotada() {
        val oferta = repositorioOfertas.buscarPorId(11)!!.copy(cantidadDisponible = 0)
        repositorioOfertas.actualizar(oferta)

        val resultado = reservarOferta(11)

        assertEquals(ResultadoOperacion.Error("Esta oferta está agotada"), resultado)
        assertEquals(5, repositorioPedidos.pedidos.value.size)
    }

    @Test
    fun informaCuandoLaOfertaNoExiste() {
        assertEquals(ResultadoOperacion.Error("Oferta no encontrada"), reservarOferta(99))
    }
}
