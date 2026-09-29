package com.rescatapp.core.model

sealed class ResultadoOperacion<out T> {
    data class Exito<out T>(val valor: T) : ResultadoOperacion<T>()

    data class Error(val mensaje: String) : ResultadoOperacion<Nothing>()
}
