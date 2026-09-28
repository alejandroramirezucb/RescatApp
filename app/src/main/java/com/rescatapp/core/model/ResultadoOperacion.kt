package com.rescatapp.core.model

sealed class ResultadoOperacion {
    data class Exito(val valor: Any) : ResultadoOperacion()

    data class Error(val mensaje: String) : ResultadoOperacion()
}
