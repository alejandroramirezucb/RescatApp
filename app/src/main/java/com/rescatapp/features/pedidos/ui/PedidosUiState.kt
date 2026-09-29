package com.rescatapp.features.pedidos.ui

import com.rescatapp.core.model.Pedido

enum class PestanaPedidos(val titulo: String) {
    ACTIVOS("Activos"),
    HISTORIAL("Historial")
}

data class PedidosUiState(
    val pestana: PestanaPedidos = PestanaPedidos.ACTIVOS,
    val activos: List<Pedido> = emptyList(),
    val historial: List<Pedido> = emptyList(),
    val mensaje: String? = null
)
