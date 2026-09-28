package com.rescatapp.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OfertaTest {
    private val oferta = Oferta(
        id = 1,
        nombre = "Pack Sorpresa",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        descripcion = "Excedente del día",
        pesoKg = 1.0,
        precioNormal = 45.0,
        precioRescate = 25.0,
        cantidadDisponible = 5,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00",
    )

    @Test
    fun calculaDescuentoYAhorroPorUnidad() {
        assertEquals(44, oferta.porcentajeDescuento)
        assertEquals(20.0, oferta.ahorroPorUnidad, 0.0)
        assertEquals(
            51,
            oferta.copy(precioNormal = 200.0, precioRescate = 99.0).porcentajeDescuento,
        )
    }

    @Test
    fun clasificaLosLimitesDeDisponibilidad() {
        assertEquals(Disponibilidad.AGOTADA, oferta.copy(cantidadDisponible = 0).disponibilidad)
        assertEquals(Disponibilidad.BAJA, oferta.copy(cantidadDisponible = 1).disponibilidad)
        assertEquals(Disponibilidad.BAJA, oferta.copy(cantidadDisponible = 2).disponibilidad)
        assertEquals(Disponibilidad.MEDIA, oferta.copy(cantidadDisponible = 3).disponibilidad)
        assertEquals(Disponibilidad.MEDIA, oferta.copy(cantidadDisponible = 4).disponibilidad)
        assertEquals(Disponibilidad.ALTA, oferta.copy(cantidadDisponible = 5).disponibilidad)
        assertEquals(Disponibilidad.ALTA, oferta.copy(cantidadDisponible = 6).disponibilidad)
        assertTrue(oferta.copy(cantidadDisponible = 0).estaAgotada)
        assertFalse(oferta.estaAgotada)
    }
}
