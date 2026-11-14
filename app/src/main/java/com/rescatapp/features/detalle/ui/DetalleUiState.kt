package com.rescatapp.features.detalle.ui

import com.rescatapp.core.model.Oferta
import com.rescatapp.features.pago.model.ComprobantePago
import com.rescatapp.features.pago.model.DatosQrPago
import com.rescatapp.features.pago.model.EstadoPago
import com.rescatapp.features.pago.model.MetodoPago

data class DetalleUiState(
    val oferta: Oferta? = null,
    val mensaje: String? = null,
    val mostrandoPasarelaPago: Boolean = false,
    val datosQrPago: DatosQrPago? = null,
    val comprobantePago: ComprobantePago? = null,
    val estadoPago: EstadoPago = EstadoPago.PENDIENTE,
    val metodoPagoSeleccionado: MetodoPago = MetodoPago.QR_SIMPLE,
    val errorPago: String? = null
) {
    val noEncontrada: Boolean
        get() = oferta == null
}
