package com.rescatapp.features.notificaciones.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Notificacion
import java.time.format.DateTimeFormatter

private val formatoHora = DateTimeFormatter.ofPattern("HH:mm")

@Composable
internal fun TarjetaNotificacion(
    notificacion: Notificacion,
    onMarcarLeida: () -> Unit,
    onEliminar: () -> Unit,
    onVerPedido: () -> Unit
) {
    Card(
        onClick = onMarcarLeida,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = notificacion.titulo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (notificacion.leida) FontWeight.Normal else FontWeight.Bold
                )
                Text(
                    text = notificacion.fechaHora.format(formatoHora),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(text = notificacion.mensaje, style = MaterialTheme.typography.bodyMedium)
                notificacion.pedidoId?.let { pedidoId ->
                    OutlinedButton(
                        onClick = {
                            onMarcarLeida()
                            onVerPedido()
                        }
                    ) {
                        Text("Ver pedido #$pedidoId")
                    }
                }
            }
            IconButton(onClick = onEliminar, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Eliminar")
            }
        }
    }
}
