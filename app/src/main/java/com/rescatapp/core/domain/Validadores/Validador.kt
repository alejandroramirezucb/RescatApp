package com.rescatapp.core.domain.validadores

import com.rescatapp.core.domain.Respuesta

object Validador {
    private val REGEX_HORA = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

    fun validarNoVacio(texto: String, mensajeError: String): Respuesta = if (texto.isBlank()) {
        Respuesta.Error(mensajeError)
    } else {
        Respuesta.Exito(texto)
    }

    fun validarMayorCero(valor: Number, mensajeError: String): Respuesta =
        if (valor.toDouble() > 0) {
            Respuesta.Exito(valor)
        } else {
            Respuesta.Error(mensajeError)
        }

    fun validarFormatoHora(hora: String): Respuesta {
        val horaLimpia = hora.trim()

        return if (REGEX_HORA.matches(horaLimpia)) {
            Respuesta.Exito(horaLimpia)
        } else {
            Respuesta.Error("Usa el formato HH:mm")
        }
    }
}
