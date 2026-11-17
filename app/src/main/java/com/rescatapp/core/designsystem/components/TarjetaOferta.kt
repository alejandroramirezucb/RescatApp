package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Disponibilidad
import com.rescatapp.core.model.Oferta

@Composable
fun TarjetaOferta(
    oferta: Oferta,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mostrarHoraLimite: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp, pressedElevation = 0.dp)
    ) {
        ImagenCategoria(
            categoria = oferta.categoria,
            imagenUri = oferta.imagenUri,
            modifier = Modifier.fillMaxWidth().aspectRatio(1.5f)
        ) {
            InsigniaDescuento(
                porcentajeDescuento = oferta.porcentajeDescuento,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            )
            if (mostrarHoraLimite) {
                EtiquetaHoraLimite(
                    horaLimite = oferta.horaRetiroHasta,
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                )
            }
        }
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = oferta.nombre,
                style = MaterialTheme.typography.titleSmall,
                minLines = 1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = oferta.comercio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            PreciosOferta(oferta, estiloPrecioRescate = MaterialTheme.typography.titleMedium)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${oferta.horaRetiroDesde} - ${oferta.horaRetiroHasta}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            val colorDisponibilidad = when (oferta.disponibilidad) {
                Disponibilidad.ALTA -> MaterialTheme.colorScheme.secondary

                Disponibilidad.MEDIA -> MaterialTheme.colorScheme.primary

                Disponibilidad.BAJA, Disponibilidad.AGOTADA ->
                    MaterialTheme.colorScheme.error
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(Modifier.size(6.dp).background(colorDisponibilidad, CircleShape))
                TextoDisponibilidad(oferta)
            }
        }
    }
}

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
