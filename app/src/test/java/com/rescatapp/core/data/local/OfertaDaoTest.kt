package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
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
    fun insertarTodasInsertaLaColeccionCompleta() = runTest {
        val entidades = ofertasDeEjemplo.map { it.toEntity() }
        ofertaDao.insertarTodas(entidades)

        val lista = ofertaDao.observarTodas().first()
        assertEquals(ofertasDeEjemplo.size, lista.size)
    }
}
