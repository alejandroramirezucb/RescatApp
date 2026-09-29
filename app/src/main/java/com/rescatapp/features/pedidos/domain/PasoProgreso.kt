package com.rescatapp.features.pedidos.domain

data class PasoProgreso(
    val estado: String,
    val completado: Boolean,
    val actual: Boolean
)
