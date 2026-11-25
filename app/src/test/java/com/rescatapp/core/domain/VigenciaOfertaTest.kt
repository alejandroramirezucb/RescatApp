package com.rescatapp.core.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import java.time.Instant
import java.time.LocalDate
import java.util.TimeZone
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VigenciaOfertaTest {
    private val oferta = Oferta(
        id = 1,
        nombre = "Pack",
        comercio = "Panaderia",
        categoria = Categoria.PANADERIA,
        descripcion = "Pan del dia",
        pesoKg = 1.0,
        precioNormal = 20.0,
        precioRescate = 10.0,
        cantidadDisponible = 1,
        fechaRetiro = LocalDate.of(2026, 11, 25),
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00"
    )

    @Test
    fun permiteReservarAntesDelInicioYDuranteElRetiro() {
        assertTrue(oferta.sigueVigente(Instant.parse("2026-11-25T21:00:00Z")))
        assertTrue(oferta.sigueVigente(Instant.parse("2026-11-25T23:59:59Z")))
    }

    @Test
    fun venceExactamenteAlCierreEnBoliviaAunqueEnUtcSeaOtroDia() {
        assertFalse(oferta.sigueVigente(Instant.parse("2026-11-26T00:00:00Z")))
        assertFalse(oferta.sigueVigente(Instant.parse("2026-11-26T00:01:00Z")))
    }

    @Test
    fun noDependeDeLaZonaHorariaDelTelefono() {
        val anterior = TimeZone.getDefault()
        try {
            TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"))
            assertTrue(oferta.sigueVigente(Instant.parse("2026-11-25T23:59:00Z")))
        } finally {
            TimeZone.setDefault(anterior)
        }
    }

    @Test
    fun horariosInvalidosNoLanzanExcepcionesNiAparecenVigentes() {
        val ahora = Instant.parse("2026-11-25T21:00:00Z")
        for (hora in listOf("", "abc", "24:00", "20:00basura")) {
            assertFalse(oferta.copy(horaRetiroHasta = hora).sigueVigente(ahora))
            assertFalse(oferta.copy(horaRetiroDesde = hora).sigueVigente(ahora))
        }
        assertFalse(oferta.copy(horaRetiroHasta = "18:00").sigueVigente(ahora))
        assertFalse(oferta.copy(horaRetiroHasta = "17:59").sigueVigente(ahora))
    }
}
