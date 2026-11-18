package com.rescatapp.core.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UsuarioDemo {
    const val NOMBRE = "Invitado"
    const val CORREO = "invitado@example.com"

    const val NEGOCIO_DEMO = "Panadería La Central"

    var negocioActual: String = NEGOCIO_DEMO
        private set

    fun actualizarNegocio(nombre: String?) {
        negocioActual = nombre ?: NEGOCIO_DEMO
    }

    private val rolActual = MutableStateFlow(RolUsuario.CLIENTE)
    val rol: StateFlow<RolUsuario> = rolActual.asStateFlow()

    fun cambiarRol(nuevoRol: RolUsuario) {
        rolActual.value = nuevoRol
    }
}
