package com.rescatapp.features.carrito.ui

import com.rescatapp.core.model.Carrito
import com.rescatapp.features.pago.model.EstadoPasarela

data class CarritoUiState(
    val carrito: Carrito = Carrito(),
    val mensaje: String? = null,
    val pasarela: EstadoPasarela = EstadoPasarela(),
    val confirmando: Boolean = false
)
