package com.rescatapp.features.pago.model

data class EstadoPasarela(
    val visible: Boolean = false,
    val metodo: MetodoPago = MetodoPago.QR_SIMPLE,
    val datosQr: DatosQrPago? = null,
    val estado: EstadoPago = EstadoPago.PENDIENTE,
    val comprobante: ComprobantePago? = null,
    val error: String? = null
)
