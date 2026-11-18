package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta

@Preview(name = "Tarjeta Oferta Panadería -44%")
@Composable
private fun TarjetaOfertaPreview() {
    val oferta = Oferta(
        id = 14,
        nombre = "Pack Sorpresa",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        descripcion = "Surtido de panes y facturas del día",
        pesoKg = 1.0,
        precioNormal = 45.0,
        precioRescate = 25.0,
        cantidadDisponible = 5,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00"
    )
    RescatAppTheme {
        TarjetaOferta(
            oferta = oferta,
            onClick = {},
            modifier = Modifier.width(172.dp)
        )
    }
}

@Preview(name = "Tarjeta Oferta con Hora Límite", fontScale = 1.3f)
@Composable
private fun TarjetaOfertaConHoraLimitePreview() {
    val oferta = Oferta(
        id = 12,
        nombre = "Pizza Familiar",
        comercio = "Pizzería Don Marco",
        categoria = Categoria.COMIDA,
        descripcion = "Pizza familiar de la casa",
        pesoKg = 1.2,
        precioNormal = 80.0,
        precioRescate = 45.0,
        cantidadDisponible = 2,
        horaRetiroDesde = "20:00",
        horaRetiroHasta = "22:00"
    )
    RescatAppTheme {
        TarjetaOferta(
            oferta = oferta,
            onClick = {},
            modifier = Modifier.width(172.dp),
            mostrarHoraLimite = true
        )
    }
}
