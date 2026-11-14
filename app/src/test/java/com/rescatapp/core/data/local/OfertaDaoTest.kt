package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.Categoria
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OfertaDaoTest {

    private lateinit var database: RescatAppDatabase
    private lateinit var ofertaDao: OfertaDao

    @Before
    fun crearBaseDatos() {
        val contexto: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(contexto, RescatAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        ofertaDao = database.ofertaDao()
    }

    @After
    fun cerrarBaseDatos() {
        database.close()
    }

    private fun crearOferta(
        id: Int = 0,
        nombre: String = "Pack Sorpresa",
        categoria: Categoria = Categoria.PANADERIA
    ) = OfertaEntity(
        id = id,
        nombre = nombre,
        comercio = "Panadería La Central",
        categoria = categoria,
        descripcion = "Surtido",
        pesoKg = 1.0,
        precioNormal = 40.0,
        precioRescate = 20.0,
        cantidadDisponible = 4,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00"
    )

    @Test
    fun insertarYObservarTodasOrdenadasPorIdDescendente() = runTest {
        val o1 = crearOferta(id = 1, nombre = "Oferta A")
        val o2 = crearOferta(id = 2, nombre = "Oferta B")
        val o3 = crearOferta(id = 3, nombre = "Oferta C")

        ofertaDao.insertar(o1)
        ofertaDao.insertar(o2)
        ofertaDao.insertar(o3)

        val lista = ofertaDao.observarTodas().first()
        assertEquals(3, lista.size)
        assertEquals(listOf(3, 2, 1), lista.map { it.id })
    }

    @Test
    fun observarPorIdDevuelveOfertaCorrectaONullSiNoExiste() = runTest {
        val o1 = crearOferta(id = 10, nombre = "Empanadas")
        ofertaDao.insertar(o1)

        val encontrada = ofertaDao.observarPorId(10).first()
        assertNotNull(encontrada)
        assertEquals("Empanadas", encontrada?.nombre)

        val noExiste = ofertaDao.observarPorId(999).first()
        assertNull(noExiste)
    }

    @Test
    fun actualizarModificaLosCamposDeLaOferta() = runTest {
        val o1 = crearOferta(id = 5, nombre = "Original")
        ofertaDao.insertar(o1)

        val modificada = o1.copy(nombre = "Modificada", cantidadDisponible = 1)
        ofertaDao.actualizar(modificada)

        val recuperada = ofertaDao.buscarPorId(5)
        assertNotNull(recuperada)
        assertEquals("Modificada", recuperada?.nombre)
        assertEquals(1, recuperada?.cantidadDisponible)
    }

    @Test
    fun eliminarRemueveLaOfertaDeLaBaseDeDatos() = runTest {
        val o1 = crearOferta(id = 7, nombre = "A borrar")
        ofertaDao.insertar(o1)

        ofertaDao.eliminar(o1)

        val lista = ofertaDao.observarTodas().first()
        assertEquals(0, lista.size)
        assertNull(ofertaDao.buscarPorId(7))
    }

    @Test
    fun filtrarConCategoriaNulaYTextoVacioDevuelveTodasLasOfertas() = runTest {
        ofertaDao.insertar(
            crearOferta(id = 1, nombre = "Croissant", categoria = Categoria.PANADERIA)
        )
        ofertaDao.insertar(crearOferta(id = 2, nombre = "Pizza", categoria = Categoria.COMIDA))
        ofertaDao.insertar(crearOferta(id = 3, nombre = "Tarta", categoria = Categoria.POSTRES))

        val resultado = ofertaDao.filtrar(categoria = null as String?, texto = "").first()
        assertEquals(3, resultado.size)
    }

    @Test
    fun filtrarPorCategoriaDevuelveSoloOfertasDeEsaCategoria() = runTest {
        ofertaDao.insertar(
            crearOferta(id = 1, nombre = "Croissant", categoria = Categoria.PANADERIA)
        )
        ofertaDao.insertar(crearOferta(id = 2, nombre = "Pizza", categoria = Categoria.COMIDA))
        ofertaDao.insertar(
            crearOferta(id = 3, nombre = "Pan baguette", categoria = Categoria.PANADERIA)
        )

        val resultado = ofertaDao.filtrar(categoria = Categoria.PANADERIA, texto = "").first()
        assertEquals(2, resultado.size)
        assertEquals(listOf(3, 1), resultado.map { it.id })
    }

    @Test
    fun filtrarPorTextoDevuelveCoincidenciasParcialesSinImportarMayusculas() = runTest {
        ofertaDao.insertar(
            crearOferta(id = 1, nombre = "Croissant de queso", categoria = Categoria.PANADERIA)
        )
        ofertaDao.insertar(
            crearOferta(id = 2, nombre = "Pizza familiar", categoria = Categoria.COMIDA)
        )
        ofertaDao.insertar(
            crearOferta(id = 3, nombre = "Sándwich de queso", categoria = Categoria.COMIDA)
        )

        val resultado = ofertaDao.filtrar(categoria = null as String?, texto = "queso").first()
        assertEquals(2, resultado.size)
        assertEquals(listOf(3, 1), resultado.map { it.id })
    }

    @Test
    fun filtrarConCategoriaYTextoAplicaAmbosCriteriosConWhere() = runTest {
        ofertaDao.insertar(
            crearOferta(id = 1, nombre = "Croissant de queso", categoria = Categoria.PANADERIA)
        )
        ofertaDao.insertar(
            crearOferta(id = 2, nombre = "Pizza familiar", categoria = Categoria.COMIDA)
        )
        ofertaDao.insertar(
            crearOferta(id = 3, nombre = "Sándwich de queso", categoria = Categoria.COMIDA)
        )

        val resultado = ofertaDao.filtrar(categoria = Categoria.COMIDA, texto = "queso").first()
        assertEquals(1, resultado.size)
        assertEquals("Sándwich de queso", resultado.first().nombre)
    }

    @Test
    fun insertarTodasInsertaLaColeccionCompleta() = runTest {
        val entidades = ofertasDeEjemplo.map { it.toEntity() }
        ofertaDao.insertarTodas(entidades)

        val lista = ofertaDao.observarTodas().first()
        assertEquals(ofertasDeEjemplo.size, lista.size)
    }

    @Test
    fun filtrarBuscaTambienEnElComercioYRespetaLaCategoria() = runTest {
        ofertaDao.insertar(
            crearOferta(id = 1, nombre = "Sandwich", categoria = Categoria.COMIDA)
                .copy(comercio = "Café Aroma")
        )
        ofertaDao.insertar(crearOferta(id = 2, nombre = "Tarta", categoria = Categoria.POSTRES))

        val resultado = ofertaDao.filtrar(Categoria.COMIDA, "café").first()
        assertEquals(listOf(1), resultado.map { it.id })
        assertEquals(0, ofertaDao.filtrar(Categoria.POSTRES, "café").first().size)
    }

    @Test
    fun unaOfertaEliminadaNoReapareceAlReabrirLaBaseDeDatos() = runTest {
        val contexto: Context = ApplicationProvider.getApplicationContext()
        val nombre = "ofertas-eliminacion-${java.util.UUID.randomUUID()}.db"
        try {
            val primera = Room.databaseBuilder(contexto, RescatAppDatabase::class.java, nombre)
                .allowMainThreadQueries()
                .build()
            try {
                primera.ofertaDao().insertar(crearOferta(id = 1))
                primera.ofertaDao().insertar(crearOferta(id = 2, nombre = "Otra oferta"))
                val repositorio = RepositorioOfertas(primera.ofertaDao())
                repositorio.eliminar(repositorio.buscarPorId(1)!!)
            } finally {
                primera.close()
            }

            val segunda = Room.databaseBuilder(contexto, RescatAppDatabase::class.java, nombre)
                .allowMainThreadQueries()
                .build()
            try {
                val repositorio = RepositorioOfertas(segunda.ofertaDao())
                assertNull(repositorio.buscarPorId(1))
                assertEquals(listOf(2), repositorio.ofertas.first().map { it.id })
            } finally {
                segunda.close()
            }
        } finally {
            contexto.deleteDatabase(nombre)
        }
    }

    @Test
    fun cambiarDisponibilidadNoPermiteSobrevenderLaUltimaUnidad() = runTest {
        ofertaDao.insertar(crearOferta(id = 1).copy(cantidadDisponible = 1))

        assertEquals(1, ofertaDao.cambiarUnidadesDisponibles(1, -1))
        assertEquals(0, ofertaDao.cambiarUnidadesDisponibles(1, -1))
        assertEquals(0, ofertaDao.buscarPorId(1)?.cantidadDisponible)
        assertEquals(1, ofertaDao.cambiarUnidadesDisponibles(1, 1))
        assertEquals(1, ofertaDao.buscarPorId(1)?.cantidadDisponible)
    }
}
