package com.rescatapp.core.domain.Validadores

import com.rescatapp.core.domain.Respuesta

object Validadores{

    private val REGEX_HORA = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

    fun validarNoVacio(texto : String , mensajeError : String) : Respuesta {
        if (texto.isEmpty()){
            return Respuesta.Error(mensajeError);
        }
        else{
             return Respuesta.Exito(texto);
        }
    }


}