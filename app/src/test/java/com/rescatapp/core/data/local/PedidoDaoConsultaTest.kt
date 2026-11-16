package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import java.time.LocalDate
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
class PedidoDaoConsultaTest {

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
    fun buscarPorIdDevuelveEntidadOEsNulo() = runTest {
        val pedido = PedidoEntity(
            id = 99,
            ofertaId = 2,
            nombreOferta = "Pasteles",
            comercio = "Dulcería",
            categoria = Categoria.PANADERIA,
            precioPagado = 18.0,
            ahorro = 12.0,
            pesoKg = 0.6,
            horaRetiroDesde = "19:00",
            horaRetiroHasta = "20:00",
            estado = EstadoPedido.PREPARANDO
        )

        pedidoDao.insertar(pedido)

        val encontrado = pedidoDao.buscarPorId(99)
        assertNotNull(encontrado)
        assertEquals("Pasteles", encontrado?.nombreOferta)

        val inexistente = pedidoDao.buscarPorId(9999)
        assertNull(inexistente)
    }

    @Test
    fun eliminarPorIdRemueveElPedido() = runTest {
        val pedido = PedidoEntity(
            id = 7,
            ofertaId = 1,
            nombreOferta = "Sopa",
            comercio = "Restaurante",
            categoria = Categoria.COMIDA,
            precioPagado = 12.0,
            ahorro = 8.0,
            pesoKg = 0.5,
            horaRetiroDesde = "13:00",
            horaRetiroHasta = "14:00",
            estado = EstadoPedido.RESERVADO
        )

        pedidoDao.insertar(pedido)
        val eliminadas = pedidoDao.eliminarPorId(7)
        assertEquals(1, eliminadas)

        val despuesDeEliminar = pedidoDao.buscarPorId(7)
        assertNull(despuesDeEliminar)
    }

    @Test
    fun preservaFechaDelPedidoAlGuardarYConsultar() = runTest {
        val fechaHistorica = LocalDate.of(2026, 11, 2)
        val pedido = PedidoEntity(
            id = 50,
            ofertaId = 3,
            nombreOferta = "Sandwich",
            comercio = "Cafe",
            categoria = Categoria.CAFETERIA,
            precioPagado = 15.0,
            ahorro = 5.0,
            pesoKg = 0.4,
            horaRetiroDesde = "10:00",
            horaRetiroHasta = "11:00",
            estado = EstadoPedido.RESERVADO,
            fecha = fechaHistorica
        )

        pedidoDao.insertar(pedido)

        val recuperado = pedidoDao.buscarPorId(50)
        assertNotNull(recuperado)
        assertEquals(fechaHistorica, recuperado?.fecha)
    }
}
