package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.crearRepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReservarOfertaUseCaseTest {
    private val api = SupabaseApiFalsa()
    private val repositorioOfertas = crearRepositorioOfertas(api = api)
    private val repositorioPedidos = crearRepositorioPedidos(api = api)
    private val reservarOferta = ReservarOfertaUseCase(repositorioOfertas, repositorioPedidos)

    @Test
    fun reservarCreaUnPedidoYActualizaElStockLocal() = runTest {
        val resultado = reservarOferta(13)

        assertTrue(resultado is ResultadoOperacion.Exito)
        assertEquals(2, repositorioOfertas.obtenerPorId(13).first()?.cantidadDisponible)
        val pedido = repositorioPedidos.pedidos.first().single()
        assertEquals("Pack Croissants", pedido.nombreOferta)
        assertEquals(EstadoPedido.RESERVADO, pedido.estado)
    }

    @Test
    fun noReservaUnaOfertaAgotada() = runTest {
        api.ofertas.replaceAll { if (it.id == 11) it.copy(cantidadDisponible = 0) else it }

        val resultado = reservarOferta(11)

        assertEquals(ResultadoOperacion.Error("Esta oferta está agotada"), resultado)
        assertTrue(repositorioPedidos.pedidos.first().isEmpty())
    }
}
