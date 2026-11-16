package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class OfertaDaoPersistenciaTest {

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
