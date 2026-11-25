package com.rescatapp.core.model

data class ItemCarrito(val oferta: Oferta, val cantidad: Int) {
    val subtotal: Double
        get() = oferta.precioRescate * cantidad

    val ahorro: Double
        get() = oferta.ahorroPorUnidad * cantidad
}

data class Carrito(val items: List<ItemCarrito> = emptyList()) {
    val total: Double
        get() = items.sumOf { it.subtotal }

    val ahorro: Double
        get() = items.sumOf { it.ahorro }

    val unidades: Int
        get() = items.sumOf { it.cantidad }

    val comercios: String
        get() = items.map { it.oferta.comercio }.distinct().joinToString(", ")

    fun cantidadDe(ofertaId: Int): Int = items.find { it.oferta.id == ofertaId }?.cantidad ?: 0
}
