package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Oferta

@Composable
internal fun TarjetaOfertaNegocio(
    oferta: Oferta,
    esRemota: Boolean,
    habilitada: Boolean,
    procesando: Boolean,
    onEditar: () -> Unit,
    onPublicar: (() -> Unit)?,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Row(modifier = Modifier.height(110.dp)) {
                ImagenCategoria(
                    categoria = oferta.categoria,
                    imagenUri = oferta.imagenUri,
                    modifier = Modifier.width(100.dp).fillMaxSize(),
                    tamanoIcono = 36.dp
                )
                DatosOfertaNegocio(oferta, onEditar, habilitada, Modifier.weight(1f))
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (esRemota) "En línea" else "Local",
                        color = Naranja,
                        style = MaterialTheme.typography.labelMedium
                    )
                    if (procesando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp).semantics {
                                contentDescription = "Procesando oferta"
                            },
                            strokeWidth = 2.dp,
                            color = Naranja
                        )
                    }
                }
                Row {
                    if (!esRemota) {
                        onPublicar?.let {
                            TextButton(onClick = it, enabled = habilitada) {
                                Text("Publicar en línea")
                            }
                        }
                    }
                    TextButton(onClick = onEliminar, enabled = habilitada) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
