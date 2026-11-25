package com.rescatapp.core.data.remote

import com.google.gson.annotations.SerializedName
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPagoSimulado
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

data class PedidoDto(
    val id: Int,
    @SerializedName("oferta_id") val ofertaId: Int,
    @SerializedName("nombre_oferta") val nombreOferta: String,
    val comercio: String,
    val categoria: String,
    @SerializedName("precio_pagado") val precioPagado: Double,
    val ahorro: Double,
    @SerializedName("peso_kg") val pesoKg: Double,
    @SerializedName("fecha_retiro") val fechaRetiro: String,
    @SerializedName("hora_retiro_desde") val horaRetiroDesde: String,
    @SerializedName("hora_retiro_hasta") val horaRetiroHasta: String,
    @SerializedName("imagen_url") val imagenUrl: String?,
    val estado: String,
    @SerializedName("codigo_rescate") val codigoRescate: String,
    @SerializedName("creado_at") val creadoAt: String,
    @SerializedName("cliente_nombre") val clienteNombre: String? = null,
    @SerializedName("cliente_telefono") val clienteTelefono: String? = null,
    @SerializedName("estado_pago_simulado") val estadoPagoSimulado: String? = null
) {
    fun toDomain(): Pedido = Pedido(
        id = id,
        ofertaId = ofertaId,
        nombreOferta = nombreOferta,
        comercio = comercio,
        categoria = Categoria.valueOf(categoria),
        precioPagado = precioPagado,
        ahorro = ahorro,
        pesoKg = pesoKg,
        horaRetiroDesde = horaRetiroDesde.take(5),
        horaRetiroHasta = horaRetiroHasta.take(5),
        estado = EstadoPedido.valueOf(estado),
        imagenUri = imagenUrl,
        fecha = OffsetDateTime.parse(creadoAt)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDate(),
        codigoServidor = codigoRescate,
        fechaRetiro = LocalDate.parse(fechaRetiro),
        clienteNombre = clienteNombre?.trim()?.takeIf { it.isNotEmpty() },
        clienteTelefono = clienteTelefono?.trim()?.takeIf { it.isNotEmpty() },
        estadoPagoSimulado = estadoPagoSimulado?.let(EstadoPagoSimulado::valueOf)
            ?: if (estado == EstadoPedido.CANCELADO.name) {
                EstadoPagoSimulado.ANULADO
            } else {
                EstadoPagoSimulado.APROBADO
            }
    )
}
