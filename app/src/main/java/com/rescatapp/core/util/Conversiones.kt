package com.rescatapp.core.util

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

private val FORMATO_HORA =
    DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT)

fun String.aDecimalOrNull(): Double? =
    trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() }

fun String.aEnteroPositivoOrNull(): Int? = trim().toIntOrNull()?.takeIf { it > 0 }

fun String.aHoraOrNull(): LocalTime? = try {
    LocalTime.parse(trim(), FORMATO_HORA)
} catch (_: DateTimeParseException) {
    null
}
