package com.rescatapp.core.model

import kotlin.math.roundToInt

private const val PORCENTAJE_TOTAL = 100
private const val UMBRAL_DISPONIBILIDAD_BAJA = 2
private const val UMBRAL_DISPONIBILIDAD_MEDIA = 4

data class Oferta(
    val id: Int,
    val nombre: String,
    val comercio: String,
    val categoria: Categoria,
    val descripcion: String,
    val pesoKg: Double,
    val precioNormal: Double,
    val precioRescate: Double,
    val cantidadDisponible: Int,
    val horaRetiroDesde: String,
    val horaRetiroHasta: String
) {
    val porcentajeDescuento: Int
        get() = ((1 - precioRescate / precioNormal) * PORCENTAJE_TOTAL).roundToInt()

    val ahorroPorUnidad: Double
        get() = precioNormal - precioRescate

    val estaAgotada: Boolean
        get() = cantidadDisponible <= 0

    val disponibilidad: Disponibilidad
        get() = when {
            estaAgotada -> Disponibilidad.AGOTADA
            cantidadDisponible <= UMBRAL_DISPONIBILIDAD_BAJA -> Disponibilidad.BAJA
            cantidadDisponible <= UMBRAL_DISPONIBILIDAD_MEDIA -> Disponibilidad.MEDIA
            else -> Disponibilidad.ALTA
        }

    fun crearPedido(idPedido: Int, estado: EstadoPedido = EstadoPedido.RESERVADO) = Pedido(
        id = idPedido,
        ofertaId = id,
        nombreOferta = nombre,
        comercio = comercio,
        categoria = categoria,
        precioPagado = precioRescate,
        ahorro = ahorroPorUnidad,
        pesoKg = pesoKg,
        horaRetiroDesde = horaRetiroDesde,
        horaRetiroHasta = horaRetiroHasta,
        estado = estado
    )
}
