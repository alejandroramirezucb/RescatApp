package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.EncabezadoSeccion
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.components.PreciosOferta
import com.rescatapp.core.designsystem.components.TextoDisponibilidad
import com.rescatapp.core.model.Oferta

@Composable
internal fun UltimasOfertasInicio(ofertas: List<Oferta>, onVerDetalle: (Int) -> Unit) {
    if (ofertas.isEmpty()) return
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f))
            .padding(horizontal = margenLateral, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EncabezadoSeccion(
            titulo = "Últimas unidades",
            subtitulo = "Packs con pocas unidades disponibles"
        )
        ofertas.take(3).forEach { oferta ->
            Card(
                onClick = { onVerDetalle(oferta.id) },
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ImagenCategoria(
                        categoria = oferta.categoria,
                        imagenUri = oferta.imagenUri,
                        modifier = Modifier.size(88.dp).clip(MaterialTheme.shapes.medium)
                    )
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = oferta.nombre,
                            style = MaterialTheme.typography.titleSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = oferta.comercio,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        PreciosOferta(oferta)
                        TextoDisponibilidad(oferta)
                    }
                }
            }
        }
    }
}
