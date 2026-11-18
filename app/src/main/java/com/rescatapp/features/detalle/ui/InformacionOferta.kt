package com.rescatapp.features.detalle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.components.InsigniaDescuento
import com.rescatapp.core.designsystem.components.PreciosOferta
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero

@Composable
internal fun InformacionOferta(oferta: Oferta, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ImagenCategoria(
                categoria = oferta.categoria,
                imagenUri = oferta.imagenUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .clip(MaterialTheme.shapes.extraLarge),
                tamanoIcono = 80.dp
            ) {
                InsigniaDescuento(
                    porcentajeDescuento = oferta.porcentajeDescuento,
                    modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = oferta.categoria.etiqueta,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                Text(text = oferta.nombre, style = MaterialTheme.typography.headlineSmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Storefront,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = oferta.comercio,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PreciosOferta(oferta, estiloPrecioRescate = MaterialTheme.typography.titleLarge)
                    Text(
                        text = "Ahorras ${oferta.ahorroPorUnidad.formatearDinero()} por pack",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Sobre este pack", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = oferta.descripcion,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RetiroOferta(oferta)
        }
    }
}
