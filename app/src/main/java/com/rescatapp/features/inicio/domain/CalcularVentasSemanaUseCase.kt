package com.rescatapp.features.inicio.domain

import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

private const val DIAS_SEMANA = 7L

class CalcularVentasSemanaUseCase @Inject constructor() {
    operator fun invoke(pedidos: List<Pedido>, hoy: LocalDate = LocalDate.now()): List<Double> {
        val pedidosSemana = pedidos.filter {
            it.estado != EstadoPedido.CANCELADO &&
                it.fecha > hoy.minusDays(DIAS_SEMANA) &&
                it.fecha <= hoy
        }
        return DayOfWeek.entries.map { dia ->
            pedidosSemana.filter { it.fecha.dayOfWeek == dia }.sumOf { it.precioPagado }
        }
    }
}
