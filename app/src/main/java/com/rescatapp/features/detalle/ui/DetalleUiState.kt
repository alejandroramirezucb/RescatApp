package com.rescatapp.features.detalle.ui

import com.rescatapp.core.model.Oferta

data class DetalleUiState(val oferta: Oferta? = null, val mensaje: String? = null) {
    val noEncontrada: Boolean
        get() = oferta == null
}
