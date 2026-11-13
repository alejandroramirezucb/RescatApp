package com.rescatapp.core.data

import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositorioPedidosTest {

    private lateinit var fakePedidoDao: FakePedidoDao
    private lateinit var repositorio: RepositorioPedidos

    @Before
    fun preparar() {
        fakePedidoDao = FakePedidoDao()
        repositorio = RepositorioPedidos(fakePedidoDao)
    }

    @Test
    fun observarPedidosEmiteLosPedidosInicialesEnOrdenDescendente() = runTest {
        val lista = repositorio.pedidos.first()

        assertEquals(pedidosDeEjemplo.size, lista.size)
        val idsEsperados = pedidosDeEjemplo.map { it.id }.sortedDescending()
        assertEquals(idsEsperados, lista.map { it.id })
    }

    @Test
    fun agregarPedidoGeneraIdYActualizaElFlujo() = runTest {
        val nuevo = Pedido(
            id = 0,
            ofertaId = 2,
            nombreOferta = "Pack Frutas",
            comercio = "Green Kitchen",
            categoria = Categoria.COMIDA,
            precioPagado = 22.0,
            ahorro = 23.0,
            pesoKg = 2.5,
            horaRetiroDesde = "17:00",
            horaRetiroHasta = "19:00",
            estado = EstadoPedido.RESERVADO,
            fecha = LocalDate.now()
        )

        val guardado = repositorio.agregar(nuevo)
        assertTrue(guardado.id > 0)

        val listaActualizada = repositorio.pedidos.first()
        assertEquals(guardado.id, listaActualizada.first().id)
        assertEquals("Pack Frutas", listaActualizada.first().nombreOferta)
    }

    @Test
    fun buscarPorIdDevuelvePedidoCorrectoONuloSiNoExiste() = runTest {
        val encontrado = repositorio.buscarPorId(5)
        assertNotNull(encontrado)
        assertEquals(5, encontrado?.id)

        val inexistente = repositorio.buscarPorId(999)
        assertNull(inexistente)
    }

    @Test
    fun actualizarPedidoModificaElEstadoYSeReflejaEnElFlujo() = runTest {
        val pedido = repositorio.buscarPorId(4)!!
        val modificado = pedido.copy(estado = EstadoPedido.LISTO)

        repositorio.actualizar(modificado)

        val recuperado = repositorio.buscarPorId(4)
        assertEquals(EstadoPedido.LISTO, recuperado?.estado)

        val listaActualizada = repositorio.pedidos.first()
        assertEquals(EstadoPedido.LISTO, listaActualizada.find { it.id == 4 }?.estado)
    }

    @Test
    fun eliminarPedidoRemueveElRegistro() = runTest {
        val eliminado = repositorio.eliminar(1)
        assertTrue(eliminado)

        assertNull(repositorio.buscarPorId(1))
        val lista = repositorio.pedidos.first()
        assertTrue(lista.none { it.id == 1 })
    }
}
