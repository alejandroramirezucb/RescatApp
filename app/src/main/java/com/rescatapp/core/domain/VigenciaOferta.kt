package com.rescatapp.core.domain

import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.aHoraOrNull
import java.time.Instant
import java.time.ZoneId

internal fun Oferta.sigueVigente(ahora: Instant = Instant.now()): Boolean {
    val desde = horaRetiroDesde.aHoraOrNull() ?: return false
    val hasta = horaRetiroHasta.aHoraOrNull() ?: return false
    if (!hasta.isAfter(desde)) return false
    return fechaRetiro.atTime(hasta)
        .atZone(ZoneId.of("America/La_Paz"))
        .toInstant()
        .isAfter(ahora)
}
