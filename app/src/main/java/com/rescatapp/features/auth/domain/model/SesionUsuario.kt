package com.rescatapp.features.auth.domain.model

import com.rescatapp.core.model.RolUsuario

data class SesionUsuario(
    val usuarioId: String,
    val nombre: String,
    val correo: String,
    val rol: RolUsuario,
    val nombreNegocio: String? = null,
    val token: String = "",
    val fechaInicio: Long = System.currentTimeMillis()
) {
    val esNegocio: Boolean
        get() = rol == RolUsuario.NEGOCIO

    val inicial: String
        get() = (if (esNegocio && !nombreNegocio.isNullOrBlank()) nombreNegocio else nombre)
            .trim()
            .take(1)
            .uppercase()
}
