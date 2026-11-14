package com.rescatapp.features.pago.model

sealed interface ResultadoPago {
    data class Exito(val comprobante: ComprobantePago) : ResultadoPago
    data class Rechazado(val motivo: String) : ResultadoPago
}
