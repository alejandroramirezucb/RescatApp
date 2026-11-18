package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.data.pedidosDeEjemplo
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
class PedidoDaoTest {

    private lateinit var database: RescatAppDatabase
    private lateinit var pedidoDao: PedidoDao

    @Before
    fun crearBaseDatos() {
        val contexto: Context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(contexto, RescatAppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        pedidoDao = database.pedidoDao()
    }

    @After
    fun cerrarBaseDatos() {
        database.close()
    }

    @Test
    fun insertarTodosPermiteObservarYBuscarLosPedidos() = runTest {
        pedidoDao.insertarTodos(pedidosDeEjemplo.map { it.toEntity() })

        assertEquals(listOf(5, 4, 3, 2, 1), pedidoDao.observarTodos().first().map { it.id })
        assertEquals(pedidosDeEjemplo.first(), pedidoDao.buscarPorId(5)?.toDomain())
    }

    @Test
    fun eliminarExceptoQuitaLosPedidosQueYaNoEstanEnElServidor() = runTest {
        pedidoDao.insertarTodos(pedidosDeEjemplo.map { it.toEntity() })

        pedidoDao.eliminarExcepto(emptyList())

        assertEquals(0, pedidoDao.observarTodos().first().size)
        assertNull(pedidoDao.buscarPorId(5))
    }
}
