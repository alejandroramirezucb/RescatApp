package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.Rojo
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Disponibilidad
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDisponibilidad

@Composable
fun TextoDisponibilidad(oferta: Oferta) {
    Text(
        text = if (oferta.estaAgotada) {
            "Agotado"
        } else {
            oferta.cantidadDisponible.formatearDisponibilidad()
        },
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

@Preview(name = "Texto Disponibilidad - 2 unidades (Rojo)")
@Composable
private fun TextoDisponibilidadBajaPreview() {
    TextoDisponibilidad(
        oferta = Oferta(
            id = 1,
            nombre = "Ejemplo",
            comercio = "Comercio",
            categoria = Categoria.PANADERIA,
            descripcion = "Desc",
            pesoKg = 1.0,
            precioNormal = 40.0,
            precioRescate = 20.0,
            cantidadDisponible = 2,
            horaRetiroDesde = "18:00",
            horaRetiroHasta = "20:00"
        )
    )
}

@Preview(name = "Texto Disponibilidad - 0 unidades (Agotado)")
@Composable
private fun TextoDisponibilidadAgotadaPreview() {
    TextoDisponibilidad(
        oferta = Oferta(
            id = 2,
            nombre = "Ejemplo",
            comercio = "Comercio",
            categoria = Categoria.PANADERIA,
            descripcion = "Desc",
            pesoKg = 1.0,
            precioNormal = 40.0,
            precioRescate = 20.0,
            cantidadDisponible = 0,
            horaRetiroDesde = "18:00",
            horaRetiroHasta = "20:00"
        )
    )
}
