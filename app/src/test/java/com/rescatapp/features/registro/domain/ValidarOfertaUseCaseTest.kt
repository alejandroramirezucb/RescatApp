package com.rescatapp.features.registro.domain

import com.rescatapp.core.model.Categoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidarOfertaUseCaseTest {
    private val validar = ValidarOfertaUseCase()

    private val campos = CamposRegistro(
        nombre = "Pack Sorpresa",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        descripcion = "Pan del día",
        pesoKg = "1,5",
        precioNormal = "45",
        precioRescate = "25",
        cantidadDisponible = "5",
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00"
    )

    @Test
    fun aceptaDatosValidos() {
        assertFalse(validar(campos).hayErrores)
    }

    @Test
    fun muestraErroresDeVariosCampos() {
        val errores = validar(
            campos.copy(
                nombre = " ",
                precioRescate = "45",
                cantidadDisponible = "0",
                horaRetiroHasta = "17:00"
            )
        )

        assertTrue(errores.hayErrores)
        assertEquals(MensajeError.NOMBRE_VACIO, errores.nombre)
        assertEquals(MensajeError.PRECIO_RESCATE_MENOR_NORMAL, errores.precioRescate)
        assertEquals(MensajeError.CANTIDAD_INVALIDA, errores.cantidadDisponible)
        assertEquals(MensajeError.HORA_FINAL_MAYOR, errores.horaRetiroHasta)
    }

    @Test
    fun rechazaHoraYNumeroInvalidos() {
        val errores = validar(
            campos.copy(
                pesoKg = "NaN",
                precioRescate = "abc",
                horaRetiroDesde = "24:00",
                horaRetiroHasta = "25:00"
            )
        )

        assertEquals(MensajeError.PESO_VACIO, errores.pesoKg)
        assertEquals(MensajeError.PRECIO_RESCATE_VACIO, errores.precioRescate)
        assertEquals(MensajeError.FORMATO_HORA_INVALIDO, errores.horaRetiroDesde)
        assertEquals(MensajeError.FORMATO_HORA_INVALIDO, errores.horaRetiroHasta)
    }
}
