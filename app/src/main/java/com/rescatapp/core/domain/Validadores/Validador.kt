package com.rescatapp.core.domain.Validadores

import com.rescatapp.core.domain.Respuesta

object Validador{

    private val REGEX_HORA = Regex("^([01]\\d|2[0-3]):[0-5]\\d$")

    fun validarNoVacio(texto : String , mensajeError : String) : Respuesta {
        if (texto.isBlank()){
            return Respuesta.Error(mensajeError);
        }
        else{
             return Respuesta.Exito(texto);
        }
    }

    fun validarMayorCero(valor: Number ,mensajeError: String ) : Respuesta{
        if(valor.toDouble()>0){
            return Respuesta.Exito(valor);
        }
        else{
            return Respuesta.Error(mensajeError);
        }
    }

    fun validarFormatoHora(hora: String): Respuesta {
        val limpio = hora.trim()
        return if (REGEX_HORA.matches(limpio)) {
            Respuesta.Exito(hora)
        } else {
            Respuesta.Error("Usa el formato HH:mm")
        }
    }

}