package com.rescatapp.navigation

import com.rescatapp.core.model.Categoria
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RutasTest {
    @Test
    fun explorarSinCategoriaRetornaLaRutaSimple() {
        assertEquals("explorar", Rutas.explorar(null))
    }

    @Test
    fun explorarConCategoriaAgregaElParametro() {
        assertEquals("explorar?categoria=POSTRES", Rutas.explorar(Categoria.POSTRES))
        assertEquals("explorar?categoria=PANADERIA", Rutas.explorar(Categoria.PANADERIA))
    }

    @Test
    fun detalleIncluyeElIdDeLaOferta() {
        assertEquals("detalle/14", Rutas.detalle(14))
    }

    @Test
    fun cadaPestanaSeReconocePorSuRutaRegistrada() {
        val pestanaExplorar = DestinoPrincipal.desdeRuta(Rutas.EXPLORAR_POR_CATEGORIA)

        assertEquals(DestinoPrincipal.EXPLORAR, pestanaExplorar)
        assertNull(DestinoPrincipal.desdeRuta(Rutas.DETALLE))
    }
}
