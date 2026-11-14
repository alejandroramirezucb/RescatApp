package com.rescatapp.core.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidadorCamposOfertaTest {
    private val validar = ValidadorCamposOferta()

    @Test
    fun cantidadVaciaNoNumericaNegativaFraccionariaOFueraDeRangoNoEsValida() {
        for (cantidad in listOf("", " ", "abc", "-1", "2.5", "2147483648")) {
            assertEquals(
                "Cantidad: '$cantidad'",
                ValidadorCamposOferta.CANTIDAD_EDICION_INVALIDA,
                editar(cantidad = cantidad).cantidadDisponible
            )
        }
    }

    @Test
    fun precioVacioNoNumericoNoPositivoONoFinitoNoEsValido() {
        for (precio in listOf("", " ", "abc", "0", "-1", "NaN", "Infinity")) {
            assertEquals(
                "Precio: '$precio'",
                ValidadorCamposOferta.PRECIO_RESCATE_INVALIDO,
                editar(precio = precio).precioRescate
            )
        }
    }

    @Test
    fun precioIgualOSuperiorAlNormalNoEsValido() {
        for (precio in listOf("45", "46")) {
            assertEquals(
                ValidadorCamposOferta.PRECIO_RESCATE_NO_MENOR,
                editar(precio = precio).precioRescate
            )
        }
    }

    @Test
    fun horasVaciasIncompletasOFueraDeRangoTienenErrorDeFormato() {
        for (hora in listOf("", "abc", "9:00", "18:0", "24:00", "18:60")) {
            assertEquals(
                "Inicio: '$hora'",
                ValidadorCamposOferta.FORMATO_HORA_INVALIDO,
                editar(desde = hora).horaRetiroDesde
            )
            assertEquals(
                "Fin: '$hora'",
                ValidadorCamposOferta.FORMATO_HORA_INVALIDO,
                editar(hasta = hora).horaRetiroHasta
            )
        }
    }

    @Test
    fun horaFinIgualOAnteriorAlInicioNoEsValida() {
        for (hora in listOf("18:00", "17:59")) {
            assertEquals(
                ValidadorCamposOferta.HORA_FINAL_NO_POSTERIOR,
                editar(hasta = hora).horaRetiroHasta
            )
        }
    }

    @Test
    fun camposValidosNoProducenErroresYEdicionAdmiteCantidadCero() {
        assertFalse(editar().hayErrores)
        assertFalse(editar(cantidad = "0").hayErrores)
        assertFalse(editar(cantidad = " 2 ", precio = " 25,5 ", desde = " 18:00 ").hayErrores)
    }

    @Test
    fun registroConservaLaReglaDeCantidadEstrictamentePositiva() {
        val errores = validar("0", "25", 45.0, "18:00", "20:00")
        assertEquals(ValidadorCamposOferta.CANTIDAD_INVALIDA, errores.cantidadDisponible)
        assertNotNull(editar(cantidad = "abc").cantidadDisponible)
        assertNull(editar(cantidad = "0").cantidadDisponible)
    }

    private fun editar(
        cantidad: String = "5",
        precio: String = "25",
        desde: String = "18:00",
        hasta: String = "20:00"
    ) = validar(cantidad, precio, 45.0, desde, hasta, permitirCantidadCero = true)
}
