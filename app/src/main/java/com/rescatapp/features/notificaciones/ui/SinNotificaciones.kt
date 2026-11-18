package com.rescatapp.features.notificaciones.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme

@Composable
internal fun SinNotificaciones() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Text(text = "Todo tranquilo por aquí", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "No tienes notificaciones en este momento. " +
                    "Aquí verás los avisos sobre pedidos y ofertas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Notificaciones vacías", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(name = "Notificaciones con texto grande", widthDp = 320, heightDp = 640, fontScale = 1.5f)
@Composable
private fun NotificacionesContenidoPreview() {
    RescatAppTheme {
        NotificacionesContenido(
            estado = NotificacionesUiState(),
            onVolver = {},
            onVerPedidos = {},
            onMarcarTodasLeidas = {},
            onLimpiarHistorial = {},
            onMarcarLeida = {},
            onEliminar = {}
        )
    }
}
