package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.EncabezadoSeccion
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.theme.bordeTarjeta
import com.rescatapp.core.model.Oferta

@Composable
internal fun ComerciosInicio(ofertas: List<Oferta>, onExplorar: () -> Unit) {
    val comercios = ofertas.groupBy { it.comercio }.entries.toList()
    if (comercios.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        EncabezadoSeccion(
            titulo = "Comercios con ofertas",
            subtitulo = "Con packs disponibles ahora",
            textoAccion = "Ver ofertas",
            onAccion = onExplorar,
            modifier = Modifier.padding(rellenoLateral)
        )
        LazyRow(
            contentPadding = rellenoLateral,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(comercios, key = { it.key }) { (nombre, packs) ->
                val referencia = packs.first()
                Card(
                    modifier = Modifier.width(168.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = bordeTarjeta(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    ImagenCategoria(
                        categoria = referencia.categoria,
                        imagenUri = referencia.imagenUri,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1.8f)
                    )
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = nombre,
                            style = MaterialTheme.typography.titleSmall,
                            minLines = 2,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = packs.map {
                                it.categoria.etiqueta
                            }.distinct().joinToString(" · "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(
                                text = if (packs.size == 1) "1 oferta" else "${packs.size} ofertas",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
