package com.rescatapp.core.util

import java.time.LocalDate
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
    }

    @Test
    fun elHorarioIndicaElDiaDeRetiro() {
        val hoy = LocalDate.now()
        assertEquals("Hoy 18:00–20:00", formatearHorario(hoy, "18:00", "20:00"))
        assertEquals("Mañana 18:00–20:00", formatearHorario(hoy.plusDays(1), "18:00", "20:00"))
        assertEquals(
            "5 mar 09:00–10:00",
            formatearHorario(LocalDate.of(2030, 3, 5), "09:00", "10:00")
        )
    }

    @Test
    fun indicaCuandoUnaOfertaEstaAgotada() {
        assertEquals("Solo quedan 3", 3.formatearDisponibilidad())
        assertEquals("Agotado", 0.formatearDisponibilidad())
    }
}
