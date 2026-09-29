package com.rescatapp.core.util

import java.util.Locale

fun Double.formatearDinero(): String = if (this % 1.0 == 0.0) {
    "Bs.${String.format(Locale.US, "%.0f", this)}"
} else {
    "Bs.${String.format(Locale.US, "%.2f", this)}"
}

fun Double.formatearPeso(): String = String.format(Locale.US, "%.1fkg", this)

fun Int.formatearDescuento(): String = "-$this%"

fun formatearHorario(desde: String, hasta: String): String = "Hoy $desde–$hasta"

fun Int.formatearDisponibilidad(): String = if (this <= 0) "Agotado" else "Solo quedan $this"
