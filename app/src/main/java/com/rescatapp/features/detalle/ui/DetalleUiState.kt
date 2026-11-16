package com.rescatapp.features.detalle.ui

import com.rescatapp.core.model.Oferta
import com.rescatapp.features.pago.model.EstadoPasarela

data class DetalleUiState(
    val oferta: Oferta? = null,
    val mensaje: String? = null,
    val pasarela: EstadoPasarela = EstadoPasarela()
) {
    val noEncontrada: Boolean
        get() = oferta == null
}
