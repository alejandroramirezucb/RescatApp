package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.data.ofertasDeEjemplo
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

    @Test
    fun insertarTodasGuardaLasOfertasOrdenadasPorIdDescendente() = runTest {
        ofertaDao.insertarTodas(ofertasDeEjemplo.map { it.toEntity() })

        val guardadas = ofertaDao.observarTodas().first()

        assertEquals(ofertasDeEjemplo.map { it.id }.sortedDescending(), guardadas.map { it.id })
        assertEquals(ofertasDeEjemplo.first(), guardadas.first().toDomain())
    }

    @Test
    fun insertarTodasReemplazaLasOfertasConElMismoId() = runTest {
        val oferta = ofertasDeEjemplo.first()
        ofertaDao.insertarTodas(listOf(oferta.toEntity()))

        ofertaDao.insertarTodas(listOf(oferta.copy(estadoPublicacion = "PAUSADA").toEntity()))

        assertEquals("PAUSADA", ofertaDao.observarTodas().first().single().estadoPublicacion)
    }

    @Test
    fun filtrarUsaCategoriaYTextoYOcultaLasPausadas() = runTest {
        ofertaDao.insertarTodas(
            ofertasDeEjemplo.map {
                if (it.id ==
                    13
                ) {
                    it.copy(estadoPublicacion = "PAUSADA").toEntity()
                } else {
                    it.toEntity()
                }
            }
        )

        val panaderia = ofertaDao.filtrar("PANADERIA", "pack").first()

        assertEquals(listOf(14, 4), panaderia.map { it.id })
    }

    @Test
    fun eliminarExceptoQuitaLasOfertasQueYaNoEstanEnElServidor() = runTest {
        ofertaDao.insertarTodas(ofertasDeEjemplo.map { it.toEntity() })

        ofertaDao.eliminarExcepto(listOf(14, 13))

        assertEquals(listOf(14, 13), ofertaDao.observarTodas().first().map { it.id })
    }
}
