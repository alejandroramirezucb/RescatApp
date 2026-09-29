package com.rescatapp.core.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularImpactoSemanalUseCaseTest {
    private val calcular = CalcularImpactoSemanalUseCase()

    @Test
    fun devuelveCeroSinPedidos() {
        val impacto = calcular(emptyList())

        assertEquals(0, impacto.reservas)
        assertEquals(0.0, impacto.ahorro, 0.0)
        assertEquals(0.0, impacto.pesoKg, 0.0)
    }

    @Test
    fun sumaLaReservaDePackCroissants() {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        ReservarOfertaUseCase(ofertas, pedidos)(13)

        val impacto = calcular(pedidos.pedidos.value)

        assertEquals(5, impacto.reservas)
        assertEquals(117.0, impacto.ahorro, 0.0)
        assertEquals(4.2, impacto.pesoKg, 0.0)
    }

    @Test
    fun excluyeLosPedidosCancelados() {
        val impacto = calcular(listOf(pedido(EstadoPedido.CANCELADO)))

        assertEquals(0, impacto.reservas)
        assertEquals(0.0, impacto.ahorro, 0.0)
        assertEquals(0.0, impacto.pesoKg, 0.0)
    }

    private fun pedido(estado: EstadoPedido) = Pedido(
        id = 1,
        ofertaId = 13,
        nombreOferta = "Pack Croissants",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        precioPagado = 15.0,
        ahorro = 15.0,
        pesoKg = 0.6,
        horaRetiroDesde = "17:00",
        horaRetiroHasta = "19:00",
        estado = estado
    )
}
