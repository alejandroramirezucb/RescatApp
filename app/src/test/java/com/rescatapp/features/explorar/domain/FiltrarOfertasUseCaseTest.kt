package com.rescatapp.features.explorar.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FiltrarOfertasUseCaseTest {
    private val filtrar = FiltrarOfertasUseCase()
    private val ofertas = RepositorioOfertas().ofertas.value

    @Test
    fun sinFiltrosRetornaLasCatorceOfertasDeMasRecienteAMasAntigua() {
        val resultado = filtrar(ofertas, "", null, OrdenOfertas.RECOMENDADAS)

        assertEquals(14, resultado.size)
        assertEquals((14 downTo 1).toList(), resultado.map { it.id })
    }

    @Test
    fun buscaCafeEnNombreOComercio() {
        val resultado = filtrar(ofertas, "café", null, OrdenOfertas.RECOMENDADAS)

        assertEquals(
            listOf("Pack Café+", "Pack Mañanero", "Sándwich Combo"),
            resultado.map { it.nombre }
        )
    }

    @Test
    fun buscaSinDistinguirMayusculas() {
        val resultado = filtrar(ofertas, "PIZZA", null, OrdenOfertas.RECOMENDADAS)

        assertEquals(
            listOf("Pizza Familiar", "2 Pizzas Medianas"),
            resultado.map { it.nombre }
        )
    }

    @Test
    fun filtraPorCategoriaPostres() {
        val resultado = filtrar(ofertas, "", Categoria.POSTRES, OrdenOfertas.RECOMENDADAS)

        assertEquals(listOf("Tortas Mix", "Caja Cupcakes"), resultado.map { it.nombre })
    }

    @Test
    fun ordenaPorDescuentoConDesempateReciente() {
        val resultado = filtrar(ofertas, "", null, OrdenOfertas.MAYOR_DESCUENTO)

        assertEquals("Pack Frutas", resultado.first().nombre)
        assertEquals(51, resultado.first().porcentajeDescuento)
        assertEquals("Pack Croissants", resultado[1].nombre)
        assertEquals(50, resultado[1].porcentajeDescuento)
        assertEquals(13, resultado[1].id)
    }

    @Test
    fun combinaBusquedaYCategoriaYRetornaVacioSiNoHayCoincidencias() {
        val resultado = filtrar(ofertas, "café", Categoria.POSTRES, OrdenOfertas.RECOMENDADAS)

        assertTrue(resultado.isEmpty())
    }
}
