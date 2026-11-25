package com.rescatapp.core.util

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeParseException

fun hoyEnBolivia(): LocalDate = LocalDate.now(ZoneId.of("America/La_Paz"))

fun String.aFechaRetiroValidaOrNull(hoy: LocalDate = hoyEnBolivia()): LocalDate? = try {
    LocalDate.parse(this).takeUnless { it.isBefore(hoy) }
} catch (_: DateTimeParseException) {
    null
}
