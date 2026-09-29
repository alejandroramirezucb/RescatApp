package com.rescatapp.core.model

object UsuarioDemo {
    const val NOMBRE = "Invitado"
    const val CORREO = "invitado@example.com"

    val inicial: String
        get() = NOMBRE.take(1)
}
