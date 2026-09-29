package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AvanzarPedidoUseCaseTest {

    private lateinit var repositorio: RepositorioPedidos
    private lateinit var useCase: AvanzarPedidoUseCase

    @Before
    fun setUp() {
        repositorio = RepositorioPedidos()
        useCase = AvanzarPedidoUseCase(repositorio)
    }

    @Test
    fun `avanzar de RESERVADO a PREPARANDO retorna Exito con el pedido actualizado`() {
        val pedido = crearPedido(id = 1, estado = EstadoPedido.RESERVADO)

        val resultado = useCase(pedido)

        assertTrue(resultado is ResultadoOperacion.Exito<*>)
        val pedidoActualizado = resultado.valorExitoso
        assertEquals(EstadoPedido.PREPARANDO, pedidoActualizado?.estado)
    }

    @Test
    fun `avanzar de PREPARANDO a LISTO retorna Exito`() {
        val pedido = crearPedido(id = 1, estado = EstadoPedido.PREPARANDO)

        val resultado = useCase(pedido)

        assertTrue(resultado is ResultadoOperacion.Exito<*>)
        val pedidoActualizado = resultado.valorExitoso
        assertEquals(EstadoPedido.LISTO, pedidoActualizado?.estado)
    }

    @Test
    fun `avanzar de LISTO a RECOGIDO retorna Exito`() {
        val pedido = crearPedido(id = 1, estado = EstadoPedido.LISTO)

        val resultado = useCase(pedido)

        assertTrue(resultado is ResultadoOperacion.Exito<*>)
        val pedidoActualizado = resultado.valorExitoso
        assertEquals(EstadoPedido.RECOGIDO, pedidoActualizado?.estado)
    }

    @Test
    fun `intentar avanzar un pedido RECOGIDO retorna Error`() {
        val pedido = crearPedido(id = 1, estado = EstadoPedido.RECOGIDO)

        val resultado = useCase(pedido)

        assertTrue(resultado is ResultadoOperacion.Error)
    }

    @Test
    fun `intentar avanzar un pedido CANCELADO retorna Error`() {
        val pedido = crearPedido(id = 1, estado = EstadoPedido.CANCELADO)

        val resultado = useCase(pedido)

        assertTrue(resultado is ResultadoOperacion.Error)
    }

    @Test
    fun `avanzar por ID actualiza el estado en el repositorio`() {
        val pedido = crearPedido(id = 0, estado = EstadoPedido.RESERVADO)
        val pedidoAgregado = repositorio.agregar(pedido)

        val resultado = useCase(pedidoAgregado.id)

        assertTrue(resultado is ResultadoOperacion.Exito<*>)
        val pedidoEnRepo = repositorio.obtenerActualPorId(pedidoAgregado.id)
        assertEquals(EstadoPedido.PREPARANDO, pedidoEnRepo?.estado)
    }

    @Test
    fun `avanzar por ID inexistente retorna Error`() {
        val resultado = useCase(999)

        assertTrue(resultado is ResultadoOperacion.Error)
        assertEquals("Pedido no encontrado", (resultado as ResultadoOperacion.Error).mensaje)
    }

    private fun crearPedido(id: Int, estado: EstadoPedido) = Pedido(
        id = id,
        ofertaId = 1,
        nombreOferta = "Comida rescate",
        comercio = "Restaurante Test",
        categoria = Categoria.COMIDA,
        precioPagado = 15.0,
        ahorro = 10.0,
        pesoKg = 0.8,
        horaRetiroDesde = "12:00",
        horaRetiroHasta = "14:00",
        estado = estado
    )
}
