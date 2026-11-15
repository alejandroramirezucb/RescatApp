package com.rescatapp.features.auth.domain.model

import com.rescatapp.core.model.RolUsuario

data class DatosRegistro(
    val nombre: String = "",
    val correo: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    val rol: RolUsuario = RolUsuario.CLIENTE,
    val telefono: String = "",
    val direccion: String = "",
    val nombreNegocio: String = ""
)
