package com.rescatapp.core.data

import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.OfertaEntity
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.Categoria
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioOfertasTest {
    private val dao = OfertaDaoFalso()
    private val repositorio = RepositorioOfertas(dao)

    @Test
    fun agregarUsaElIdGeneradoPorElDaoYColocaLaOfertaPrimero() = runTest {
        val ofertaNueva = repositorio.agregar(ofertasDeEjemplo.first().copy(id = 999))

        assertEquals(15, ofertaNueva.id)
        assertEquals(ofertaNueva, repositorio.ofertas.first().first())
        assertEquals(ofertaNueva, repositorio.buscarPorId(ofertaNueva.id))
    }

    @Test
    fun actualizarReemplazaSoloLaOfertaConElMismoId() = runTest {
        val oferta = repositorio.buscarPorId(14)!!.copy(cantidadDisponible = 0)

        repositorio.actualizar(oferta)

        assertEquals(0, repositorio.buscarPorId(14)?.cantidadDisponible)
        assertEquals(14, repositorio.ofertas.first().size)
    }

    @Test
    fun cambiarUnidadesDisponiblesSumaORestaSinDejarStockNegativo() = runTest {
        assertTrue(repositorio.cambiarUnidadesDisponibles(ofertaId = 13, diferencia = -1))
        assertFalse(repositorio.cambiarUnidadesDisponibles(ofertaId = 13, diferencia = -3))
        assertFalse(repositorio.cambiarUnidadesDisponibles(ofertaId = 99, diferencia = -1))
        assertEquals(2, repositorio.buscarPorId(13)?.cantidadDisponible)
    }

    @Test
    fun obtenerPorIdEmiteNuloCuandoLaOfertaNoExiste() = runTest {
        assertNull(repositorio.obtenerPorId(99).first())
    }

    @Test
    fun unaBaseVaciaNoSeSustituyePorDatosDeEjemplo() = runTest {
        val repositorioVacio = crearRepositorioOfertas(emptyList())

        assertTrue(repositorioVacio.ofertas.first().isEmpty())
        assertNull(repositorioVacio.buscarPorId(14))
    }

    @Test
    fun reflejaCambiosExternosDelDaoInclusoCuandoQuedaVacio() = runTest {
        val unica = ofertasDeEjemplo.first()
        val daoUnico = OfertaDaoFalso(listOf(unica))
        val repo = RepositorioOfertas(daoUnico)
        daoUnico.actualizar(unica.copy(nombre = "Cambio desde Room").toEntity())
        assertEquals("Cambio desde Room", repo.ofertas.first().single().nombre)

        daoUnico.eliminarPorId(unica.id)

        assertTrue(repo.ofertas.first().isEmpty())
        assertNull(repo.obtenerPorId(unica.id).first())
    }

    @Test
    fun unaEscrituraFallidaNoPublicaUnaOfertaEnMemoria() = runTest {
        val daoConError = object : OfertaDao by dao {
            override suspend fun insertar(oferta: OfertaEntity): Long =
                error("No se pudo escribir")
        }
        val repo = RepositorioOfertas(daoConError)

        assertTrue(runCatching { repo.agregar(ofertasDeEjemplo.first()) }.isFailure)
        assertEquals(14, repo.ofertas.first().size)
    }

    @Test
    fun filtrarDelegaCategoriaYTextoSinEspaciosAlDao() = runTest {
        val resultado = repositorio.filtrar(Categoria.PANADERIA, "  croissants  ").first()

        assertEquals(listOf("Pack Croissants"), resultado.map { it.nombre })
    }

    @Test
    fun eliminarPersisteLaAusenciaEnElDao() = runTest {
        repositorio.eliminar(14)

        assertNull(dao.buscarPorId(14))
        assertEquals(13, repositorio.ofertas.first().size)
    }
}
