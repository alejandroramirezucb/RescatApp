package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.mock.ofertasDeEjemplo
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CancelarPedidoUseCaseTest {

    private lateinit var repositorioPedidos: RepositorioPedidos
    private lateinit var repositorioOfertas: RepositorioOfertas
    private lateinit var useCase: CancelarPedidoUseCase

    @Before
    fun setUp() {
        repositorioPedidos = RepositorioPedidos()
        repositorioOfertas = RepositorioOfertas()
        useCase = CancelarPedidoUseCase(repositorioPedidos, repositorioOfertas)
    }

    @Test
    fun `cancelar pedido en estado RESERVADO cambia a CANCELADO y devuelve stock`() {
        val ofertaInicial = ofertasDeEjemplo.first()
        val stockInicial = ofertaInicial.cantidadDisponible
        val pedido =
            crearPedido(id = 100, ofertaId = ofertaInicial.id, estado = EstadoPedido.RESERVADO)
        repositorioPedidos.agregar(pedido)

        val resultado = useCase(pedido)

        assertTrue(resultado is ResultadoOperacion.Exito)
        val pedidoCancelado = (resultado as ResultadoOperacion.Exito<Pedido>).valor
        assertEquals(EstadoPedido.CANCELADO, pedidoCancelado.estado)

        val ofertaActualizada = repositorioOfertas.obtenerActualPorId(ofertaInicial.id)
        assertEquals(stockInicial + 1, ofertaActualizada?.cantidadDisponible)
    }

    @Test
    fun `intentar cancelar pedido en PREPARANDO retorna error especifico`() {
        val pedido = crearPedido(id = 101, ofertaId = 14, estado = EstadoPedido.PREPARANDO)

        val resultado = useCase(pedido)

        assertTrue(resultado is ResultadoOperacion.Error)
        val error = resultado as ResultadoOperacion.Error
        assertEquals("Solo puedes cancelar un pedido reservado", error.mensaje)
    }

    @Test
    fun `intentar cancelar pedido en LISTO retorna error especifico`() {
        val pedido = crearPedido(id = 102, ofertaId = 14, estado = EstadoPedido.LISTO)

        val resultado = useCase(pedido)

        assertTrue(resultado is ResultadoOperacion.Error)
        val error = resultado as ResultadoOperacion.Error
        assertEquals("Solo puedes cancelar un pedido reservado", error.mensaje)
    }

    @Test
    fun `intentar cancelar pedido por ID inexistente retorna error`() {
        val resultado = useCase(999)

        assertTrue(resultado is ResultadoOperacion.Error)
        val error = resultado as ResultadoOperacion.Error
        assertEquals("Pedido no encontrado", error.mensaje)
    }

    private fun crearPedido(id: Int, ofertaId: Int, estado: EstadoPedido) = Pedido(
        id = id,
        ofertaId = ofertaId,
        nombreOferta = "Oferta Test",
        comercio = "Comercio Test",
        categoria = Categoria.PANADERIA,
        precioPagado = 10.0,
        ahorro = 5.0,
        pesoKg = 0.5,
        horaRetiroDesde = "10:00",
        horaRetiroHasta = "12:00",
        estado = estado
    )
}
