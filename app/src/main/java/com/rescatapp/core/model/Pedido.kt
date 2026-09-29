package com.rescatapp.core.model

data class Pedido(
    val id: Int,
    val ofertaId: Int,
    val nombreOferta: String,
    val comercio: String,
    val categoria: Categoria,
    val precioPagado: Double,
    val ahorro: Double,
    val pesoKg: Double,
    val horaRetiroDesde: String,
    val horaRetiroHasta: String,
    val estado: EstadoPedido
) {
    val estaActivo: Boolean
        get() = !estado.esFinal
}
