package com.rescatapp.features.registro.domain

import com.rescatapp.core.domain.ValidadorCamposOferta
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidarOfertaUseCaseTest {
    private val validarOferta = ValidarOfertaUseCase()

    @Test
    fun camposValidosNoTienenErrores() {
        assertFalse(validarOferta(camposValidos).hayErrores)
    }

    @Test
    fun unFormularioVacioMuestraUnMensajePorCampo() {
        val errores = validarOferta(CamposRegistro())

        assertEquals(MensajesValidacion.NOMBRE_VACIO, errores.nombre)
        assertEquals(MensajesValidacion.CATEGORIA_SIN_ELEGIR, errores.categoria)
        assertEquals(MensajesValidacion.PESO_INVALIDO, errores.pesoKg)
        assertEquals(ValidadorCamposOferta.CANTIDAD_INVALIDA, errores.cantidadDisponible)
        assertEquals(ValidadorCamposOferta.FORMATO_HORA_INVALIDO, errores.horaRetiroHasta)
    }

    @Test
    fun elPrecioDeRescateDebeSerMenorAlNormal() {
        val errores = validarOferta(camposValidos.copy(precioNormal = "40", precioRescate = "45"))

        assertEquals(ValidadorCamposOferta.PRECIO_RESCATE_NO_MENOR, errores.precioRescate)
    }

    @Test
    fun laCantidadDebeSerUnEnteroPositivo() {
        val errores = validarOferta(camposValidos.copy(cantidadDisponible = "2.5"))

        assertEquals(ValidadorCamposOferta.CANTIDAD_INVALIDA, errores.cantidadDisponible)
    }

    @Test
    fun laHoraFinalDebeSerPosteriorALaInicial() {
        val errores = validarOferta(
            camposValidos.copy(horaRetiroDesde = "18:00", horaRetiroHasta = "17:00")
        )

        assertEquals(ValidadorCamposOferta.HORA_FINAL_NO_POSTERIOR, errores.horaRetiroHasta)
    }

    @Test
    fun unaHoraFueraDeRangoTieneFormatoInvalido() {
        val errores = validarOferta(camposValidos.copy(horaRetiroHasta = "25:00"))

        assertTrue(errores.hayErrores)
        assertEquals(ValidadorCamposOferta.FORMATO_HORA_INVALIDO, errores.horaRetiroHasta)
    }
}
