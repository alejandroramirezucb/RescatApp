package com.rescatapp.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class RescatNavHostTest {
    @Test
    fun construirRutaExplorarSinCategoriaRetornaRutaSimple() {
        assertEquals("explorar", construirRutaExplorar(null))
        assertEquals("explorar", construirRutaExplorar(""))
        assertEquals("explorar", construirRutaExplorar("   "))
    }

    @Test
    fun construirRutaExplorarConCategoriaAgregaParametro() {
        assertEquals("explorar?categoria=POSTRES", construirRutaExplorar("POSTRES"))
        assertEquals("explorar?categoria=PANADERIA", construirRutaExplorar("PANADERIA"))
    }
}
