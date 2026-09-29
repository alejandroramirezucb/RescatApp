package com.rescatapp.core.data

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositorioOfertasTest {
    private val repositorio = RepositorioOfertas()

    @Test
    fun agregarAsignaElSiguienteIdYColocaLaOfertaPrimero() {
        val ofertaNueva = repositorio.agregar(ofertasDeEjemplo.first().copy(id = 0))

        assertEquals(15, ofertaNueva.id)
        assertEquals(ofertaNueva, repositorio.ofertas.value.first())
    }

    @Test
    fun actualizarReemplazaSoloLaOfertaConElMismoId() {
        val oferta = repositorio.buscarPorId(14)!!.copy(cantidadDisponible = 0)

        repositorio.actualizar(oferta)

        assertEquals(0, repositorio.buscarPorId(14)?.cantidadDisponible)
        assertEquals(14, repositorio.ofertas.value.size)
    }

    @Test
    fun obtenerPorIdEmiteNuloCuandoLaOfertaNoExiste() = runTest {
        assertNull(repositorio.obtenerPorId(99).first())
    }

    @Test
    fun generarSiguienteIdEmpiezaEnUnoConListaVacia() {
        assertEquals(1, generarSiguienteId(emptyList()))
    }
}
