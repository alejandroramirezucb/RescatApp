package com.rescatapp.core.designsystem.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.Rojo
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.model.Disponibilidad
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDisponibilidad

@Composable
fun TextoDisponibilidad(oferta: Oferta) {
    Text(
        text = oferta.cantidadDisponible.formatearDisponibilidad(),
        style = MaterialTheme.typography.labelMedium,
        color = oferta.disponibilidad.color
    )
}

private val Disponibilidad.color: Color
    get() = when (this) {
        Disponibilidad.ALTA -> Verde
        Disponibilidad.MEDIA -> Naranja
        Disponibilidad.BAJA, Disponibilidad.AGOTADA -> Rojo
    }
