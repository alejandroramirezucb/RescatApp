package com.rescatapp.features.inicio.domain

import com.rescatapp.core.data.mock.ofertasDeEjemplo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeleccionarOfertasInicioUseCaseTest {
    private val useCase = SeleccionarOfertasInicioUseCase()

    @Test
    fun seleccionaCatorceDisponiblesYCuatroPorAgotarseConDatosDeEjemplo() {
        val (disponibles, agotando) = useCase(ofertasDeEjemplo)

        assertEquals(14, disponibles.size)
        assertEquals("Pack Sorpresa", disponibles.first().nombre)
        assertEquals(
            disponibles.map { it.id }.sortedDescending(),
            disponibles.map { it.id }
        )

        assertEquals(4, agotando.size)
        assertEquals(
            listOf("Pizza Familiar", "2 Pizzas Medianas", "Caja Cupcakes", "Burger + Papas"),
            agotando.map { it.nombre }
        )
    }

    @Test
    fun noIncluyeOfertasAgotadasEnNingunaSeccion() {
        val ofertasConAgotada = ofertasDeEjemplo.map { oferta ->
            if (oferta.id == 12) oferta.copy(cantidadDisponible = 0) else oferta
        }

        val (disponibles, agotando) = useCase(ofertasConAgotada)

        assertTrue(disponibles.none { it.id == 12 })
        assertTrue(agotando.none { it.id == 12 })
    }

    @Test
    fun retornaListasVaciasCuandoNoHayOfertas() {
        val (disponibles, agotando) = useCase(emptyList())

        assertTrue(disponibles.isEmpty())
        assertTrue(agotando.isEmpty())
    }
}
