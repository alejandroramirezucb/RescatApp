package com.rescatapp.features.explorar.domain

import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FiltrarOfertasUseCaseTest {
    private val filtrarOfertas = FiltrarOfertasUseCase()

    private fun nombresFiltrados(filtros: FiltrosOfertas) =
        filtrarOfertas(ofertasDeEjemplo, filtros).map { it.nombre }

    @Test
    fun sinFiltrosDevuelveTodasDeLaMasRecienteALaMasAntigua() {
        val ofertas = filtrarOfertas(ofertasDeEjemplo, FiltrosOfertas())

        assertEquals((14 downTo 1).toList(), ofertas.map { it.id })
    }

    @Test
    fun buscaPorNombreOComercioSinDistinguirMayusculas() {
        assertEquals(
            listOf("Pack Café+", "Pack Mañanero", "Sándwich Combo"),
            nombresFiltrados(FiltrosOfertas(texto = "café"))
        )
        assertEquals(
            listOf("Pizza Familiar", "2 Pizzas Medianas"),
            nombresFiltrados(FiltrosOfertas(texto = "PIZZA"))
        )
    }

    @Test
    fun filtraPorCategoria() {
        assertEquals(
            listOf("Tortas Mix", "Caja Cupcakes"),
            nombresFiltrados(FiltrosOfertas(categoria = Categoria.POSTRES))
        )
    }

    @Test
    fun ordenaPorMayorDescuentoYLuegoPorMasReciente() {
        val nombres = nombresFiltrados(FiltrosOfertas(orden = OrdenOfertas.MAYOR_DESCUENTO))

        assertEquals(listOf("Pack Frutas", "Pack Croissants"), nombres.take(2))
    }

    @Test
    fun combinaBusquedaYCategoria() {
        val filtros = FiltrosOfertas(texto = "café", categoria = Categoria.POSTRES)

        assertTrue(nombresFiltrados(filtros).isEmpty())
    }
}
