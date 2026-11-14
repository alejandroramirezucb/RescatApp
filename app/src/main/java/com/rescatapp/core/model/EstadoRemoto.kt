package com.rescatapp.core.model

sealed interface EstadoRemoto<out T> {
    data object Cargando : EstadoRemoto<Nothing>
    data class Exito<T>(val datos: T) : EstadoRemoto<T>
    data class Error(val mensaje: String) : EstadoRemoto<Nothing>
}
