package com.rescatapp.features.inicio.domain

import com.rescatapp.core.data.ofertasDeEjemplo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeleccionarOfertasInicioUseCaseTest {
    private val seleccionarOfertas = SeleccionarOfertasInicioUseCase()

    @Test
    fun separaLasDisponiblesDeLasQueSeEstanAgotando() {
        val ofertas = seleccionarOfertas(ofertasDeEjemplo)

        assertEquals(14, ofertas.disponibles.size)
        assertEquals("Pack Sorpresa", ofertas.disponibles.first().nombre)
        assertEquals(
            listOf("Pizza Familiar", "2 Pizzas Medianas", "Caja Cupcakes", "Burger + Papas"),
            ofertas.porAgotarse.map { it.nombre }
        )
    }

    @Test
    fun losMejoresDelDiaVanDeMayorAMenorDescuento() {
        val nombres = seleccionarOfertas(ofertasDeEjemplo).mejoresDelDia.map { it.nombre }

        assertEquals(listOf("Pack Frutas", "Pack Croissants"), nombres.take(2))
        assertEquals(14, nombres.size)
    }

    @Test
    fun puedeInteresarteSoloMuestraPostres() {
        val postres = seleccionarOfertas(ofertasDeEjemplo).postres

        assertEquals(listOf("Tortas Mix", "Caja Cupcakes"), postres.map { it.nombre })
    }

    @Test
    fun excluyeLasOfertasAgotadas() {
        val conUnaAgotada = ofertasDeEjemplo.map {
            if (it.id == 11) it.copy(cantidadDisponible = 0) else it
        }

        val ofertas = seleccionarOfertas(conUnaAgotada)

        assertEquals(13, ofertas.disponibles.size)
        assertTrue(ofertas.porAgotarse.none { it.id == 11 })
    }
}
