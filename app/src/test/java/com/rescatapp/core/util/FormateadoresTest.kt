package com.rescatapp.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormateadoresTest {
    @Test
    fun muestraDecimalesDelDineroSoloCuandoExisten() {
        assertEquals("Bs.25", 25.0.formatearDinero())
        assertEquals("Bs.12.50", 12.5.formatearDinero())
    }

    @Test
    fun formateaPesoDescuentoYHorario() {
        assertEquals("1.8kg", 1.8.formatearPeso())
        assertEquals("-44%", 44.formatearDescuento())
        assertEquals("Hoy 18:00–20:00", formatearHorario("18:00", "20:00"))
    }

    @Test
    fun indicaCuandoUnaOfertaEstaAgotada() {
        assertEquals("Solo quedan 3", 3.formatearDisponibilidad())
        assertEquals("Agotado", 0.formatearDisponibilidad())
    }
}
