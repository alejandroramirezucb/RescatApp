package com.rescatapp.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EstadoPedidoTest {
    @Test
    fun avanzaEnElOrdenDelRetiro() {
        assertEquals(EstadoPedido.PREPARANDO, EstadoPedido.RESERVADO.siguiente())
        assertEquals(EstadoPedido.LISTO, EstadoPedido.PREPARANDO.siguiente())
        assertEquals(EstadoPedido.RECOGIDO, EstadoPedido.LISTO.siguiente())
    }

    @Test
    fun losEstadosFinalesNoAvanzan() {
        assertNull(EstadoPedido.RECOGIDO.siguiente())
        assertNull(EstadoPedido.CANCELADO.siguiente())
        assertTrue(EstadoPedido.CANCELADO.esFinal)
        assertFalse(EstadoPedido.LISTO.esFinal)
    }

    @Test
    fun elHistorialMuestraCompletadoParaUnPedidoRecogido() {
        assertEquals("Completado", EstadoPedido.RECOGIDO.etiquetaHistorial)
        assertEquals("Cancelado", EstadoPedido.CANCELADO.etiquetaHistorial)
    }
}
