package com.rescatapp.core.domain.Parseadores

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object  Parseador{


    private val FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm")


    fun aDecimal(texto: String): Double? {
        val normalizado = texto.trim().replace(',', '.')
        return normalizado.toDoubleOrNull()
    }

    fun aEntero(texto: String): Int? {
        return texto.trim().toIntOrNull()
    }


    fun aHora(texto: String): LocalTime? {
        return try {
            LocalTime.parse(texto.trim(), FORMATO_HORA)
        } catch (e: Exception) {
            null
        }
    }
}