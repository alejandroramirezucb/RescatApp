package com.rescatapp.features.pago.model

data class ComprobantePago(
    val numeroTransaccion: String,
    val referencia: String,
    val monto: Double,
    val comercio: String,
    val fechaHora: String
)
