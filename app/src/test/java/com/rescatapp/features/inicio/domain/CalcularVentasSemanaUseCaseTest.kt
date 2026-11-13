package com.rescatapp.features.inicio.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularVentasSemanaUseCaseTest {
    private val calcularVentasSemana = CalcularVentasSemanaUseCase()
    private val miercoles = LocalDate.of(2026, 11, 11)

    private fun pedido(fecha: LocalDate, estado: EstadoPedido = EstadoPedido.RECOGIDO) = Pedido(
        id = 1,
        ofertaId = 1,
        nombreOferta = "Pack",
        comercio = "Comercio",
        categoria = Categoria.COMIDA,
        precioPagado = 25.0,
        ahorro = 20.0,
        pesoKg = 1.0,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00",
        estado = estado,
        fecha = fecha
    )

    @Test
    fun sumaElTotalVendidoPorDiaDeLaSemana() {
        val pedidos = listOf(
            pedido(miercoles),
            pedido(miercoles),
            pedido(miercoles.minusDays(2))
        )

        val ventas = calcularVentasSemana(pedidos, hoy = miercoles)

        assertEquals(listOf(25.0, 0.0, 50.0, 0.0, 0.0, 0.0, 0.0), ventas)
    }

    @Test
    fun noCuentaLosPedidosCancelados() {
        val pedidos = listOf(pedido(miercoles), pedido(miercoles, EstadoPedido.CANCELADO))

        val ventas = calcularVentasSemana(pedidos, hoy = miercoles)

        assertEquals(25.0, ventas[2], 0.0)
    }

    @Test
    fun noCuentaLosPedidosDeHaceMasDeUnaSemana() {
        val ventas = calcularVentasSemana(listOf(pedido(miercoles.minusDays(7))), hoy = miercoles)

        assertEquals(0.0, ventas.sum(), 0.0)
    }
}
