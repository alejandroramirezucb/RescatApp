package com.rescatapp.core.util

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ConversionesTest {
    @Test
    fun aceptaDecimalesConComaOPunto() {
        assertEquals(1.5, "1,5".aDecimalOrNull()!!, 0.0)
        assertEquals(1.5, " 1.5 ".aDecimalOrNull()!!, 0.0)
        assertNull("abc".aDecimalOrNull())
    }

    @Test
    fun rechazaDecimalesQueNoSonPositivos() {
        assertNull("0".aDecimalPositivoOrNull())
        assertNull("-2".aDecimalPositivoOrNull())
    }

    @Test
    fun soloAceptaEnterosPositivos() {
        assertEquals(4, "4".aEnteroPositivoOrNull())
        listOf("", "abc", "2.5", "0", "-1").forEach { assertNull(it.aEnteroPositivoOrNull()) }
    }

    @Test
    fun soloAceptaHorasConFormatoDeDosDigitos() {
        assertEquals(LocalTime.of(9, 0), "09:00".aHoraOrNull())
        listOf("9:00", "25:00", "12:60", "18h").forEach { assertNull(it.aHoraOrNull()) }
    }

    @Test
    fun normalizaHorasRemotasSinAceptarSufijosInvalidos() {
        assertEquals("09:05", "09:05".horaRemotaNormalizada())
        assertEquals("09:05", "09:05:00".horaRemotaNormalizada())
        assertEquals("23:59", "23:59:59.123456".horaRemotaNormalizada())
        for (hora in listOf("24:00:00", "12:60:00", "20:00basura", "", "9:00")) {
            assertEquals("", hora.horaRemotaNormalizada())
        }
    }
}
