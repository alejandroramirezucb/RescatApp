package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero

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

@Composable
private fun DatosOfertaNegocio(
    oferta: Oferta,
    onEditar: () -> Unit,
    habilitada: Boolean,
    modifier: Modifier
) {
    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        EncabezadoOferta(oferta.nombre, oferta.estaAgotada)
        Text(
            text = "Quedan: ${oferta.cantidadDisponible}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        PieOferta(oferta.precioRescate.formatearDinero(), onEditar, habilitada)
    }
}

@Composable
private fun EncabezadoOferta(nombre: String, estaAgotada: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = nombre,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(8.dp))
        ChipEstadoOferta(estaAgotada)
    }
}

@Composable
private fun PieOferta(precio: String, onEditar: () -> Unit, habilitada: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = precio,
            style = MaterialTheme.typography.titleMedium,
            color = Naranja
        )
        Surface(
            color = Color.Transparent,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            onClick = onEditar,
            enabled = habilitada
        ) {
            Text(
                text = "Editar",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun ChipEstadoOferta(estaAgotada: Boolean = false) {
    val color = if (estaAgotada) Color(0xFFE53935) else Color(0xFF4CAF50)
    val texto = if (estaAgotada) "Agotada" else "Activa"
    Surface(color = color.copy(alpha = 0.12f), shape = RoundedCornerShape(50)) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
