package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.rescatapp.core.model.Categoria
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        ImagenCategoria(
            categoria = oferta.categoria,
            modifier = Modifier.fillMaxWidth().height(104.dp)
        ) {
            InsigniaDescuento(
                porcentajeDescuento = oferta.porcentajeDescuento,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            )
            MarcadorFavorito(modifier = Modifier.align(Alignment.TopEnd).padding(8.dp))
            if (mostrarHoraLimite) {
                EtiquetaHoraLimite(
                    horaLimite = oferta.horaRetiroHasta,
                    modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                )
            }
        }
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = oferta.nombre,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = oferta.comercio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            PreciosOferta(oferta)
            TextoDisponibilidad(oferta)
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
    TarjetaOferta(
        oferta = oferta,
        onClick = {},
        modifier = Modifier.width(172.dp)
    )
}

@Preview(name = "Tarjeta Oferta con Hora Límite")
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
    TarjetaOferta(
        oferta = oferta,
        onClick = {},
        modifier = Modifier.width(172.dp),
        mostrarHoraLimite = true
    )
}

@Composable
private fun MarcadorFavorito(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.FavoriteBorder,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}
