package com.rescatapp.core.data

import org.junit.Assert.assertEquals
import org.junit.Test

class RepositorioOfertasTest {
    @Test
    fun emiteOfertasActualizadas() {
        val repositorio = RepositorioOfertas()
        val oferta = repositorio.obtenerPorId(13)!!

        repositorio.actualizar(oferta.copy(cantidadDisponible = 2))

        assertEquals(2, repositorio.ofertas.value.find { it.id == 13 }?.cantidadDisponible)
    }

    @Test
    fun emiteOfertasPublicadas() {
        val repositorio = RepositorioOfertas()
        val oferta = repositorio.obtenerPorId(14)!!

        val publicada = repositorio.agregar(oferta.copy(id = 0, nombre = "Nueva oferta"))

        assertEquals(15, publicada.id)
        assertEquals(15, repositorio.ofertas.value.size)
        assertEquals("Nueva oferta", repositorio.ofertas.value.first().nombre)
    }
}
