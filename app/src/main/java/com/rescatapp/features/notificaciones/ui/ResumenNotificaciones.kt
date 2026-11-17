package com.rescatapp.features.notificaciones.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ResumenNotificaciones(
    conteoNoLeidas: Int,
    onMarcarTodasLeidas: () -> Unit,
    onLimpiarHistorial: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = when (conteoNoLeidas) {
                0 -> "Estás al día"
                1 -> "Tienes 1 notificación sin leer"
                else -> "Tienes $conteoNoLeidas notificaciones sin leer"
            },
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "Toca un aviso para marcarlo como leído.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (conteoNoLeidas > 0) {
                TextButton(onClick = onMarcarTodasLeidas) {
                    Icon(Icons.Outlined.DoneAll, contentDescription = null, Modifier.size(20.dp))
                    Spacer(Modifier.size(8.dp))
                    Text("Marcar todas como leídas")
                }
            }
            TextButton(onClick = onLimpiarHistorial) {
                Icon(Icons.Outlined.DeleteSweep, contentDescription = null, Modifier.size(20.dp))
                Spacer(Modifier.size(8.dp))
                Text("Limpiar historial")
            }
        }
    }
}
