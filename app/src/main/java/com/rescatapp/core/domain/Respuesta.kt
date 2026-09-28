package com.rescatapp.core.domain

sealed class Respuesta {
    data class Exito(val valor: Any) : Respuesta()

    data class Error(val mensaje: String) : Respuesta()
}
