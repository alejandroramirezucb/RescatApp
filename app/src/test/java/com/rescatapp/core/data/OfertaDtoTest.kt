package com.rescatapp.core.data

import com.rescatapp.core.data.remote.OfertaDto
import com.rescatapp.core.domain.sigueVigente
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class OfertaDtoTest {
    @Test
    fun convierteHorasDePostgresConSegundosYFraccionesAlFormatoDelFormulario() {
        val remota = ofertaRemota().copy(
            horaRetiroDesde = "09:05:00",
            horaRetiroHasta = "20:30:00.000000"
        )
        val oferta = remota.toDomain()
        assertEquals("09:05", oferta.horaRetiroDesde)
        assertEquals("20:30", oferta.horaRetiroHasta)
    }

    @Test
    fun noConvierteUnPrefijoValidoDeUnaHoraCorruptaEnUnaOfertaVigente() {
        val remota = ofertaRemota().copy(horaRetiroHasta = "20:00basura")
        val oferta = remota.toDomain()
        assertEquals("", oferta.horaRetiroHasta)
        assertFalse(oferta.sigueVigente(Instant.EPOCH))
    }

    private fun ofertaRemota() = OfertaDto(
        id = 1,
        nombre = "Pack",
        categoria = "PANADERIA",
        descripcion = "Pan",
        pesoKg = 1.0,
        precioNormal = 20.0,
        precioRescate = 10.0,
        cantidadDisponible = 1,
        fechaRetiro = "2026-11-25",
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00",
        imagenUrl = null,
        estado = "ACTIVA",
        negocios = null
    )
}
