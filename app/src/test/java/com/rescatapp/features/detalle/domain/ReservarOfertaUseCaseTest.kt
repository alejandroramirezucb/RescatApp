package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReservarOfertaUseCaseTest {
    @Test
    fun creaPedidoYActualizaDisponibilidad() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val reservar = ReservarOfertaUseCase(ofertas, pedidos)

        val pedido = reservar(13)

        assertEquals(EstadoPedido.RESERVADO, pedido?.estado)
        assertEquals(2, ofertas.obtenerPorId(13)?.cantidadDisponible)
        assertEquals(1, pedidos.pedidos.value.size)
    }

    @Test
    fun noReservaUnaOfertaAgotada() {
        val ofertas = RepositorioOfertas()
        val reservar = ReservarOfertaUseCase(ofertas, RepositorioPedidos())
        val oferta = ofertas.obtenerPorId(13)!!
        ofertas.actualizar(oferta.copy(cantidadDisponible = 0))

        assertNull(reservar(13))
    }
}
