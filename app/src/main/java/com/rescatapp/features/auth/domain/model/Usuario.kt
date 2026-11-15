package com.rescatapp.features.auth.domain.model

import com.rescatapp.core.model.RolUsuario

data class Usuario(
    val id: String,
    val nombre: String,
    val correo: String,
    val contrasenaHash: String,
    val rol: RolUsuario,
    val telefono: String = "",
    val direccion: String = "",
    val nombreNegocio: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)
