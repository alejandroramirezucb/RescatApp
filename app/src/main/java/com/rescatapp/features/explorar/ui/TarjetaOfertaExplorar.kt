package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.components.InsigniaDescuento
import com.rescatapp.core.designsystem.components.PreciosOferta
import com.rescatapp.core.designsystem.components.TextoDisponibilidad
import com.rescatapp.core.model.Disponibilidad
import com.rescatapp.core.model.Oferta

@Composable
internal fun TarjetaOfertaExplorar(oferta: Oferta, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 0.dp)
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
        }
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = oferta.nombre,
                    style = MaterialTheme.typography.titleSmall,
                    minLines = 2,
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
            }
            PreciosOferta(oferta, estiloPrecioRescate = MaterialTheme.typography.titleLarge)
            val colorEstado = when (oferta.disponibilidad) {
                Disponibilidad.ALTA -> MaterialTheme.colorScheme.secondary
                Disponibilidad.MEDIA -> MaterialTheme.colorScheme.primary
                Disponibilidad.BAJA, Disponibilidad.AGOTADA -> MaterialTheme.colorScheme.error
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(Modifier.size(6.dp).background(colorEstado, CircleShape))
                TextoDisponibilidad(oferta)
            }
        }
    }
}
