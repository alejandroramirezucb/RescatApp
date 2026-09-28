package com.rescatapp.core.domain.parseadores

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object Parseador {
    private val FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm")

    fun aDecimal(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()

    fun aEntero(texto: String): Int? = texto.trim().toIntOrNull()

    fun aHora(texto: String): LocalTime? = try {
        LocalTime.parse(texto.trim(), FORMATO_HORA)
    } catch (_: Exception) {
        null
    }
}
