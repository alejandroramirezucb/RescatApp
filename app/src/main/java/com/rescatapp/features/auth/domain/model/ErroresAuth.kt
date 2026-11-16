package com.rescatapp.features.auth.domain.model

data class ErroresLogin(val correo: String? = null, val contrasena: String? = null) {
    val hayErrores: Boolean
        get() = correo != null || contrasena != null
}

data class ErroresRegistro(
    val nombre: String? = null,
    val correo: String? = null,
    val contrasena: String? = null,
    val confirmarContrasena: String? = null,
    val nombreNegocio: String? = null
) {
    val hayErrores: Boolean
        get() = listOf(nombre, correo, contrasena, confirmarContrasena, nombreNegocio)
            .any { it != null }
}
