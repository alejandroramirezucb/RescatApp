package com.rescatapp.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EstadoPedidoTest {
    @Test
    fun avanzaSoloHastaRecogido() {
        assertEquals(EstadoPedido.PREPARANDO, EstadoPedido.RESERVADO.siguiente())
        assertEquals(EstadoPedido.LISTO, EstadoPedido.PREPARANDO.siguiente())
        assertEquals(EstadoPedido.RECOGIDO, EstadoPedido.LISTO.siguiente())
        assertNull(EstadoPedido.RECOGIDO.siguiente())
        assertNull(EstadoPedido.CANCELADO.siguiente())
    }
}
