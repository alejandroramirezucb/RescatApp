package com.rescatapp.features.detalle.domain

import com.rescatapp.core.model.Oferta

data class DetalleUiState(
    val oferta: Oferta? = null,
    val noEncontrada: Boolean = false,
    val mensaje: String? = null
)
