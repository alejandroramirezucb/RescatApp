package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservarOfertaUseCaseTest {
    private val repositorioOfertas = crearRepositorioOfertas()
    private val repositorioPedidos = RepositorioPedidos(FakePedidoDao())
    private val reservarOferta = ReservarOfertaUseCase(repositorioOfertas, repositorioPedidos)

    @Test
    fun reservarDescuentaUnaUnidadYCreaUnPedidoReservado() = runTest {
        val resultado = reservarOferta(13)

        assertTrue(resultado is ResultadoOperacion.Exito)
        assertEquals(2, repositorioOfertas.buscarPorId(13)?.cantidadDisponible)
        val pedidoNuevo = repositorioPedidos.pedidos.first().first()
        assertEquals("Pack Croissants", pedidoNuevo.nombreOferta)
        assertEquals(EstadoPedido.RESERVADO, pedidoNuevo.estado)
        assertEquals(6, pedidoNuevo.id)
    }

    @Test
    fun noReservaUnaOfertaAgotada() = runTest {
        val oferta = repositorioOfertas.buscarPorId(11)!!.copy(cantidadDisponible = 0)
        repositorioOfertas.actualizar(oferta)

        val resultado = reservarOferta(11)

        assertEquals(ResultadoOperacion.Error("Esta oferta está agotada"), resultado)
        assertEquals(5, repositorioPedidos.pedidos.first().size)
    }

    @Test
    fun informaCuandoLaOfertaNoExiste() = runTest {
        assertEquals(ResultadoOperacion.Error("Oferta no encontrada"), reservarOferta(99))
    }
}
