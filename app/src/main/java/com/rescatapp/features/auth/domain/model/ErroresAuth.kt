package com.rescatapp.features.auth.domain.model

data class ErroresLogin(
    val correo: String? = null,
    val contrasena: String? = null,
    val general: String? = null
) {
    val hayErrores: Boolean
        get() = correo != null || contrasena != null || general != null
}

data class ErroresRegistro(
    val nombre: String? = null,
    val correo: String? = null,
    val contrasena: String? = null,
    val confirmarContrasena: String? = null,
    val nombreNegocio: String? = null,
    val general: String? = null
) {
    val hayErrores: Boolean
        get() = nombre != null ||
            correo != null ||
            contrasena != null ||
            confirmarContrasena != null ||
            nombreNegocio != null ||
            general != null
}
