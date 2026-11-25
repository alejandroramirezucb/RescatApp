package com.rescatapp.core.util

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle

private val FORMATO_HORA =
    DateTimeFormatter.ofPattern("HH:mm").withResolverStyle(ResolverStyle.STRICT)

fun String.aDecimalOrNull(): Double? =
    trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() }

fun String.aDecimalPositivoOrNull(): Double? = aDecimalOrNull()?.takeIf { it > 0 }

fun String.aEnteroPositivoOrNull(): Int? = trim().toIntOrNull()?.takeIf { it > 0 }

fun String.aHoraOrNull(): LocalTime? = try {
    LocalTime.parse(trim(), FORMATO_HORA)
} catch (_: DateTimeParseException) {
    null
}

fun String.horaRemotaNormalizada(): String = try {
    LocalTime.parse(trim(), DateTimeFormatter.ISO_LOCAL_TIME).format(FORMATO_HORA)
} catch (_: DateTimeParseException) {
    ""
}

fun hoyEnBolivia(): LocalDate = LocalDate.now(ZoneId.of("America/La_Paz"))

fun String.aFechaRetiroValidaOrNull(hoy: LocalDate = hoyEnBolivia()): LocalDate? = try {
    LocalDate.parse(this).takeUnless { it.isBefore(hoy) }
} catch (_: DateTimeParseException) {
    null
}
