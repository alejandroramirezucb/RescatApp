package com.rescatapp.features.pedidos.ui

import com.rescatapp.core.model.PasoProgreso
import com.rescatapp.core.model.Pedido

enum class PestanaPedidos(val titulo: String, val mensajeSinPedidos: String) {
    ACTIVOS("Activos", "Aún no tienes pedidos activos"),
    HISTORIAL("Historial", "Aún no tienes pedidos en tu historial")
}

data class PedidoConProgreso(val pedido: Pedido, val pasos: List<PasoProgreso>)

data class PedidosUiState(
    val pestana: PestanaPedidos = PestanaPedidos.ACTIVOS,
    val activos: List<PedidoConProgreso> = emptyList(),
    val historial: List<Pedido> = emptyList(),
    val mensaje: String? = null
)
