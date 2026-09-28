package com.rescatapp.core.model

enum class EstadoPedido(val etiqueta: String, val etiquetaHistorial: String = etiqueta) {
    RESERVADO("Reservado"),
    PREPARANDO("Preparando"),
    LISTO("Listo"),
    RECOGIDO("Recogido", "Completado"),
    CANCELADO("Cancelado");

    fun siguiente(): EstadoPedido? = when (this) {
        RESERVADO -> PREPARANDO
        PREPARANDO -> LISTO
        LISTO -> RECOGIDO
        RECOGIDO, CANCELADO -> null
    }
}
