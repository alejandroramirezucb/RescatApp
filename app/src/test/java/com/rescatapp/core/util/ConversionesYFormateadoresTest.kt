package com.rescatapp.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConversionesYFormateadoresTest {
    @Test
    fun convierteTextoValido() {
        assertEquals(1.5, "1,5".aDecimalOrNull())
        assertEquals(1.5, "1.5".aDecimalOrNull())
        assertEquals(4, "4".aEnteroPositivoOrNull())
        assertEquals("23:59", "23:59".aHoraOrNull().toString())
    }

    @Test
    fun rechazaTextoInvalido() {
        assertNull("abc".aDecimalOrNull())
        assertNull("0".aEnteroPositivoOrNull())
        assertNull("2.5".aEnteroPositivoOrNull())
        assertNull("9:00".aHoraOrNull())
        assertNull("25:00".aHoraOrNull())
    }

    @Test
    fun formateaValoresDelDiseno() {
        assertEquals("Bs.25", 25.0.formatearDinero())
        assertEquals("Bs.12.50", 12.5.formatearDinero())
        assertEquals("1.8kg", 1.8.formatearPeso())
        assertEquals("0.0kg", 0.0.formatearPeso())
        assertEquals("-44%", 44.formatearDescuento())
        assertEquals("Hoy 18:00–20:00", formatearHorario("18:00", "20:00"))
        assertEquals("Solo quedan 3", 3.formatearDisponibilidad())
        assertEquals("Agotado", 0.formatearDisponibilidad())
    }
}
