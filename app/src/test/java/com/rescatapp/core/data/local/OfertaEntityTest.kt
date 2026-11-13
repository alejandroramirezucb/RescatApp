package com.rescatapp.core.data.local

import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.Categoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OfertaEntityTest {

    @Test
    fun mapeaTodosLosCamposEntreEntidadYModeloDeDominio() {
        val original = ofertasDeEjemplo.first()
        val entidad = original.toEntity()

        assertEquals(original.id, entidad.id)
        assertEquals(original.nombre, entidad.nombre)
        assertEquals(original.comercio, entidad.comercio)
        assertEquals(original.categoria, entidad.categoria)
        assertEquals(original.descripcion, entidad.descripcion)
        assertEquals(original.pesoKg, entidad.pesoKg, 0.001)
        assertEquals(original.precioNormal, entidad.precioNormal, 0.001)
        assertEquals(original.precioRescate, entidad.precioRescate, 0.001)
        assertEquals(original.cantidadDisponible, entidad.cantidadDisponible)
        assertEquals(original.horaRetiroDesde, entidad.horaRetiroDesde)
        assertEquals(original.horaRetiroHasta, entidad.horaRetiroHasta)
        assertEquals(original.imagenUri, entidad.imagenUri)

        val reconstruido = entidad.toDomain()
        assertEquals(original, reconstruido)
    }

    @Test
    fun entidadConValoresPorDefectoGeneraIdCeroEImagenNula() {
        val entidad = OfertaEntity(
            nombre = "Croissants",
            comercio = "Panadería Central",
            categoria = Categoria.PANADERIA,
            descripcion = "Deliciosos",
            pesoKg = 0.5,
            precioNormal = 30.0,
            precioRescate = 15.0,
            cantidadDisponible = 5,
            horaRetiroDesde = "18:00",
            horaRetiroHasta = "20:00"
        )

        assertEquals(0, entidad.id)
        assertNull(entidad.imagenUri)
    }
}
