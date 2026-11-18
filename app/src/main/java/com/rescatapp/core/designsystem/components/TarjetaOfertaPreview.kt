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
    val oferta = ofertaEjemplo
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
    val oferta = ofertaEjemplo.copy(
        nombre = "Pizza Familiar",
        categoria = Categoria.COMIDA,
        cantidadDisponible = 2
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
