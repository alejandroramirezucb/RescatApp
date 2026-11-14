package com.rescatapp.features.pago.model

data class DatosQrPago(
    val referenciaTransaccion: String,
    val codigoQrPayload: String,
    val monto: Double,
    val comercio: String,
    val concepto: String,
    val expiracionSegundos: Int = 300,
    val fechaHoraEmision: String
) {
    val estaExpirado: Boolean
        get() = expiracionSegundos <= 0
}
