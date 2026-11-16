package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.model.Categoria
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OfertaDaoFiltroTest {

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
}
