package com.rescatapp.core.data

import com.rescatapp.core.model.EstadoPedido
import org.junit.Assert.assertEquals
import org.junit.Test

class DatosDeEjemploTest {
    @Test
    fun losDescuentosCoincidenConElDiseno() {
        val descuentosEsperados = listOf(44, 50, 44, 50, 49, 45, 50, 44, 49, 50, 50, 46, 51, 44)

        assertEquals(descuentosEsperados, ofertasDeEjemplo.map { it.porcentajeDescuento })
    }

    @Test
    fun losPedidosCopianPrecioAhorroYPesoDeSuOferta() {
        val packSorpresa = pedidosDeEjemplo.first { it.id == 5 }

        assertEquals("Pack Sorpresa", packSorpresa.nombreOferta)
        assertEquals(25.0, packSorpresa.precioPagado, 0.0)
        assertEquals(20.0, packSorpresa.ahorro, 0.0)
        assertEquals(EstadoPedido.LISTO, packSorpresa.estado)
    }
}
