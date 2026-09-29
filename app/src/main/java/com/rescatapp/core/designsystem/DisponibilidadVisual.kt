package com.rescatapp.core.designsystem

import androidx.compose.ui.graphics.Color
import com.rescatapp.core.model.Disponibilidad

fun Disponibilidad.colorDisponibilidad(): Color = when (this) {
    Disponibilidad.ALTA -> VerdeRescat
    Disponibilidad.MEDIA -> NaranjaRescat
    Disponibilidad.BAJA, Disponibilidad.AGOTADA -> RojoTextoRescat
}
