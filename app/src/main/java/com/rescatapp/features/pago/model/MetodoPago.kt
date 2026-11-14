package com.rescatapp.features.pago.model

enum class MetodoPago(val titulo: String, val subtitulo: String, val descripcion: String) {
    QR_SIMPLE(
        titulo = "QR Simple",
        subtitulo = "Transferencia bancaria inmediata",
        descripcion = "Escanea el código QR desde cualquier app bancaria para pagar " +
            "de inmediato."
    ),
    EFECTIVO(
        titulo = "Efectivo al retirar",
        subtitulo = "Pago presencial en el local",
        descripcion = "Paga el importe exacto en efectivo al momento de retirar tu pedido " +
            "en el comercio."
    )
}
