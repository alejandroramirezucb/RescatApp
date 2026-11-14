package com.rescatapp.features.inicio.ui

import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.UsuarioDemo
import java.time.LocalDate

data class InicioNegocioUiState(
    val nombreNegocio: String = UsuarioDemo.NEGOCIO_DEMO,
    val ventasHoy: String = "Bs.0",
    val cantidadPedidos: Int = 0,
    val ofertasActivas: Int = 0,
    val rescatados: Int = 0,
    val pedidosRecientes: List<Pedido> = emptyList(),
    val ventasSemana: List<Double> = List(7) { 0.0 },
    val indiceDiaActual: Int = LocalDate.now().dayOfWeek.ordinal,
    val notificacionesNoLeidas: Int = 0
)
