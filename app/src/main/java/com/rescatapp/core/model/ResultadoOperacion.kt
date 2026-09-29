package com.rescatapp.core.model

sealed class ResultadoOperacion<out T> {
    abstract val valorExitoso: T?

    data class Exito<out T>(val valor: T) : ResultadoOperacion<T>() {
        override val valorExitoso: T = valor
    }

    data class Error(val mensaje: String) : ResultadoOperacion<Nothing>() {
        override val valorExitoso: Nothing? = null
    }
}
