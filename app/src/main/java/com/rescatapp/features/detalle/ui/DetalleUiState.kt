package com.rescatapp.features.detalle.ui

import com.rescatapp.core.model.Oferta

data class DetalleUiState(
    val oferta: Oferta? = null,
    val enCarrito: Int = 0,
    val mensaje: String? = null
)
