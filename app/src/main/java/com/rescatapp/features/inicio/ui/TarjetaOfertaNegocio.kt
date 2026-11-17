package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun TarjetaOfertaNegocio(
    oferta: Oferta,
    esRemota: Boolean,
    habilitada: Boolean,
    procesando: Boolean,
    onEditar: () -> Unit,
    onPublicar: (() -> Unit)?,
    onEliminar: () -> Unit,
    onCambiarEstado: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ImagenCategoria(
                    categoria = oferta.categoria,
                    imagenUri = oferta.imagenUri,
                    modifier = Modifier.size(80.dp).clip(MaterialTheme.shapes.medium),
                    tamanoIcono = 36.dp
                )
                DatosOfertaNegocio(oferta, onEditar, habilitada, Modifier.weight(1f))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (esRemota) {
                            "${oferta.estadoPublicacion} | Retiro ${oferta.fechaRetiro}"
                        } else {
                            "Local"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (procesando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp).semantics {
                                contentDescription = "Procesando oferta"
                            },
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                onCambiarEstado?.let {
                    TextButton(onClick = it, enabled = habilitada) {
                        Text(if (oferta.estadoPublicacion == "PAUSADA") "Reactivar" else "Pausar")
                    }
                }
                if (!esRemota) {
                    onPublicar?.let {
                        TextButton(onClick = it, enabled = habilitada) {
                            Text("Publicar en línea")
                        }
                    }
                }
                TextButton(onClick = onEliminar, enabled = habilitada) {
                    Text(
                        "Eliminar",
                        color = if (habilitada) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        }
                    )
                }
            }
        }
    }
}

@Preview(name = "Oferta del negocio", showBackground = true, widthDp = 360)
@Preview(name = "Oferta del negocio con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun TarjetaOfertaNegocioPreview() {
    RescatAppTheme {
        TarjetaOfertaNegocio(
            oferta = Oferta(
                id = 1,
                nombre = "Pack de panes artesanales y repostería del día",
                comercio = "Panadería del barrio",
                categoria = Categoria.PANADERIA,
                descripcion = "Una selección para compartir.",
                pesoKg = 1.0,
                precioNormal = 40.0,
                precioRescate = 20.0,
                cantidadDisponible = 3,
                horaRetiroDesde = "18:00",
                horaRetiroHasta = "20:00"
            ),
            esRemota = false,
            habilitada = true,
            procesando = false,
            onEditar = {},
            onPublicar = {},
            onEliminar = {}
        )
    }
}
