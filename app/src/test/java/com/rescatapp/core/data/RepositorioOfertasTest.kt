package com.rescatapp.core.data

import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.OfertaEntity
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.Categoria
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
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
    fun cambiarUnidadesDisponiblesSumaORestaSobreLaCantidadActual() {
        repositorio.cambiarUnidadesDisponibles(ofertaId = 13, diferencia = -1)
        repositorio.cambiarUnidadesDisponibles(ofertaId = 99, diferencia = -1)

        assertEquals(2, repositorio.buscarPorId(13)?.cantidadDisponible)
    }

    @Test
    fun obtenerPorIdEmiteNuloCuandoLaOfertaNoExiste() = runTest {
        assertNull(repositorio.obtenerPorId(99).first())
    }

    @Test
    fun generarSiguienteIdEmpiezaEnUnoConListaVacia() {
        assertEquals(1, generarSiguienteId(emptyList()))
    }

    @Test
    fun conDaoPersisteAgregarActualizarYEliminar() = runTest {
        val fakeDao = FakeOfertaDao()
        fakeDao.items.value = ofertasDeEjemplo.map { it.toEntity() }
        val testDispatcher = StandardTestDispatcher(testScheduler)
        val repoConDao = RepositorioOfertas(
            ofertaDao = fakeDao,
            coroutineScope = CoroutineScope(testDispatcher),
            ofertasIniciales = ofertasDeEjemplo
        )
        testScheduler.advanceUntilIdle()

        // Agregar
        val nueva = repoConDao.agregar(
            ofertasDeEjemplo.first().copy(id = 0, nombre = "Oferta Room")
        )
        testScheduler.advanceUntilIdle()
        assertEquals("Oferta Room", fakeDao.items.value.first().nombre)

        // Actualizar
        repoConDao.actualizar(nueva.copy(nombre = "Oferta Modificada"))
        testScheduler.advanceUntilIdle()
        assertEquals("Oferta Modificada", fakeDao.buscarPorId(nueva.id)?.nombre)

        // Eliminar
        repoConDao.eliminar(nueva.id)
        testScheduler.advanceUntilIdle()
        assertNull(fakeDao.buscarPorId(nueva.id))
    }

    @Test
    fun conDaoFiltrarDelegaEnDao() = runTest {
        val fakeDao = FakeOfertaDao()
        fakeDao.items.value = ofertasDeEjemplo.map { it.toEntity() }
        val repoConDao = RepositorioOfertas(
            ofertaDao = fakeDao,
            coroutineScope = TestScope(StandardTestDispatcher(testScheduler)),
            ofertasIniciales = ofertasDeEjemplo
        )

        val resultado = repoConDao.filtrar(Categoria.PANADERIA, "").first()
        assertEquals(
            ofertasDeEjemplo.filter { it.categoria == Categoria.PANADERIA }.size,
            resultado.size
        )
    }

    private class FakeOfertaDao : OfertaDao {
        val items = MutableStateFlow<List<OfertaEntity>>(emptyList())

        override suspend fun insertar(oferta: OfertaEntity): Long {
            items.update { listOf(oferta) + it.filter { o -> o.id != oferta.id } }
            return oferta.id.toLong()
        }

        override suspend fun insertarTodas(ofertas: List<OfertaEntity>) {
            items.update { ofertas + it }
        }

        override suspend fun actualizar(oferta: OfertaEntity) {
            items.update { lista -> lista.map { if (it.id == oferta.id) oferta else it } }
        }

        override suspend fun eliminar(oferta: OfertaEntity) {
            items.update { lista -> lista.filter { it.id != oferta.id } }
        }

        override suspend fun eliminarPorId(id: Int): Int {
            val antes = items.value.size
            items.update { lista -> lista.filter { it.id != id } }
            return antes - items.value.size
        }

        override fun observarTodas(): Flow<List<OfertaEntity>> = items

        override fun observarPorId(id: Int): Flow<OfertaEntity?> =
            items.map { lista -> lista.find { it.id == id } }

        override suspend fun buscarPorId(id: Int): OfertaEntity? = items.value.find { it.id == id }

        override fun filtrar(categoria: String?, texto: String?): Flow<List<OfertaEntity>> =
            items.map { lista ->
                lista.filter {
                    (categoria == null || it.categoria.name == categoria) &&
                        (texto.isNullOrEmpty() || it.nombre.contains(texto, ignoreCase = true))
                }
            }
    }
}
