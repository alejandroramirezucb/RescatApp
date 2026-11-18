package com.rescatapp.core.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun Double.formatearDinero(): String = if (this % 1.0 == 0.0) {
    "Bs.${toInt()}"
} else {
    String.format(Locale.US, "Bs.%.2f", this)
}

fun Double.formatearPeso(): String = String.format(Locale.US, "%.1fkg", this)

fun Int.formatearDescuento(): String = "-$this%"

fun formatearHorario(fecha: LocalDate, desde: String, hasta: String): String {
    val hoy = LocalDate.now()
    val dia = when (fecha) {
        hoy -> "Hoy"
        hoy.plusDays(1) -> "Mañana"
        else -> fecha.format(DateTimeFormatter.ofPattern("d MMM", Locale.forLanguageTag("es")))
    }
    return "$dia $desde–$hasta"
}

fun Int.formatearDisponibilidad(): String = if (this <= 0) "Agotado" else "Solo quedan $this"
