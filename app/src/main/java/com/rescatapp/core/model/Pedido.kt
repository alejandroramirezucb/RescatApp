package com.rescatapp.core.model

import java.time.LocalDate

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
    val estado: EstadoPedido,
    val imagenUri: String? = null,
    val fecha: LocalDate = LocalDate.now()
) {
    val estaActivo: Boolean
        get() = !estado.esFinal

    val codigoRescate: String
        get() = "#APV-100$id"
}
