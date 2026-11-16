package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    fun insertarYObservarTodosOrdenadoPorIdDescendente() = runTest {
        val p1 = PedidoEntity(
            id = 1,
            ofertaId = 1,
            nombreOferta = "Oferta 1",
            comercio = "Comercio 1",
            categoria = Categoria.COMIDA,
            precioPagado = 20.0,
            ahorro = 10.0,
            pesoKg = 0.8,
            horaRetiroDesde = "18:00",
            horaRetiroHasta = "19:00",
            estado = EstadoPedido.RESERVADO
        )
        val p2 = p1.copy(id = 2, nombreOferta = "Oferta 2")
        val p3 = p1.copy(id = 3, nombreOferta = "Oferta 3")

        pedidoDao.insertar(p1)
        pedidoDao.insertar(p2)
        pedidoDao.insertar(p3)

        val lista = pedidoDao.observarTodos().first()
        assertEquals(3, lista.size)
        assertEquals(listOf(3, 2, 1), lista.map { it.id })
    }

    @Test
    fun actualizarEstadoDeUnPedido() = runTest {
        val pedido = PedidoEntity(
            id = 10,
            ofertaId = 5,
            nombreOferta = "Empanadas",
            comercio = "Pastelería",
            categoria = Categoria.COMIDA,
            precioPagado = 25.0,
            ahorro = 15.0,
            pesoKg = 1.0,
            horaRetiroDesde = "20:00",
            horaRetiroHasta = "21:00",
            estado = EstadoPedido.RESERVADO
        )

        pedidoDao.insertar(pedido)

        val pedidoActualizado = pedido.copy(estado = EstadoPedido.LISTO)
        pedidoDao.actualizar(pedidoActualizado)

        val recuperado = pedidoDao.buscarPorId(10)
        assertNotNull(recuperado)
        assertEquals(EstadoPedido.LISTO, recuperado?.estado)
    }

    @Test
    fun insertarTodosPermiteCargarDatosDeEjemplo() = runTest {
        val entities = pedidosDeEjemplo.map { it.toEntity() }
        pedidoDao.insertarTodos(entities)

        val todos = pedidoDao.observarTodos().first()
        assertEquals(pedidosDeEjemplo.size, todos.size)
        val idsEsperados = pedidosDeEjemplo.map { it.id }.sortedDescending()
        assertEquals(idsEsperados, todos.map { it.id })
    }
}
