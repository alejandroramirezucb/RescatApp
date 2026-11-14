package com.rescatapp.core.model

enum class TipoNotificacion(val tituloPorDefecto: String) {
    PEDIDO_RESERVADO("Reserva confirmada"),
    PEDIDO_EN_PREPARACION("Pedido en preparación"),
    PEDIDO_LISTO("Pedido listo para retirar"),
    PEDIDO_RECOGIDO("Pedido completado"),
    PEDIDO_CANCELADO("Pedido cancelado"),
    NUEVO_PEDIDO_VENDEDOR("Nuevo pedido recibido"),
    OFERTA_AGOTADA("Oferta agotada"),
    OFERTA_PUBLICADA("Oferta publicada"),
    SISTEMA("Aviso de RescatApp")
}
