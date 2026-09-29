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
    fun creaPedidoYActualizaDisponibilidad() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val reservar = ReservarOfertaUseCase(ofertas, pedidos)

        val resultado = reservar(13)

        assertTrue(resultado is ResultadoOperacion.Exito)
        val pedido = (resultado as ResultadoOperacion.Exito).valor as Pedido
        assertEquals(EstadoPedido.RESERVADO, pedido.estado)
        assertEquals(2, ofertas.obtenerActualPorId(13)?.cantidadDisponible)
        assertEquals(6, pedidos.pedidos.value.size)
    }

    @Test
    fun noReservaUnaOfertaAgotada() {
        val ofertas = RepositorioOfertas()
        val reservar = ReservarOfertaUseCase(ofertas, RepositorioPedidos())
        val oferta = ofertas.obtenerActualPorId(13)!!
        ofertas.actualizar(oferta.copy(cantidadDisponible = 0))

        assertEquals(
            ResultadoOperacion.Error("Esta oferta está agotada"),
            reservar(13)
        )
    }

    @Test
    fun informaSiLaOfertaNoExiste() {
        val reservar = ReservarOfertaUseCase(RepositorioOfertas(), RepositorioPedidos())

        assertEquals(ResultadoOperacion.Error("Oferta no encontrada"), reservar(999))
    }
}
