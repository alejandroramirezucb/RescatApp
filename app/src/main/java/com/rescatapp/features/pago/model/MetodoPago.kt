package com.rescatapp.features.pago.model

enum class MetodoPago(val titulo: String, val subtitulo: String) {
    QR_SIMPLE("QR Simple", "Transferencia bancaria inmediata"),
    EFECTIVO("Efectivo al retirar", "Pago presencial en el local")
}
