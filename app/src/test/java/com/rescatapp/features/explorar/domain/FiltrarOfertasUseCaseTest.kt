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
    fun ordenaLasOfertasMasRecientes() {
        val resultado = filtrar(ofertas, "", null, OrdenOfertas.RECOMENDADAS)

        assertEquals(14, resultado.size)
        assertEquals(14, resultado.first().id)
        assertEquals(1, resultado.last().id)
    }

    @Test
    fun combinaBusquedaYCategoria() {
        assertEquals(3, filtrar(ofertas, "café", null, OrdenOfertas.RECOMENDADAS).size)
        assertEquals(2, filtrar(ofertas, "", Categoria.POSTRES, OrdenOfertas.RECOMENDADAS).size)
        assertTrue(filtrar(ofertas, "café", Categoria.POSTRES, OrdenOfertas.RECOMENDADAS).isEmpty())
    }

    @Test
    fun ordenaPorDescuentoConDesempateReciente() {
        val resultado = filtrar(ofertas, "", null, OrdenOfertas.MAYOR_DESCUENTO)

        assertEquals("Pack Frutas", resultado.first().nombre)
        assertEquals("Pack Croissants", resultado[1].nombre)
    }
}
