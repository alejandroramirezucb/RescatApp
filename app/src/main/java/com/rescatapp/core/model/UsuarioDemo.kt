package com.rescatapp.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UsuarioDemo {
    const val NOMBRE = "Invitado"
    const val CORREO = "invitado@example.com"

    val inicial: String
        get() = NOMBRE.take(1)

    private val _rol = MutableStateFlow(RolUsuario.CLIENTE)
    val rol: StateFlow<RolUsuario> = _rol.asStateFlow()

    fun cambiarRol(nuevoRol: RolUsuario) {
        _rol.value = nuevoRol
    }
}
