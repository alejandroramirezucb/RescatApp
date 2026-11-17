package com.rescatapp.features.notificaciones.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BotonSecundario
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val formatoFechaNotificacion = DateTimeFormatter.ofPattern(
    "dd MMM yyyy · HH:mm",
    Locale.forLanguageTag("es-BO")
)

@Composable
internal fun TarjetaNotificacion(
    notificacion: Notificacion,
    onMarcarLeida: () -> Unit,
    onEliminar: () -> Unit,
    onVerPedido: () -> Unit
) {
    val esquema = MaterialTheme.colorScheme
    Card(
        onClick = onMarcarLeida,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = if (notificacion.leida) esquema.surface else esquema.primaryContainer
        ),
        border = BorderStroke(
            1.dp,
            if (notificacion.leida) esquema.outlineVariant else esquema.primary.copy(alpha = 0.35f)
        ),
        modifier = Modifier.fillMaxWidth().semantics {
            stateDescription = if (notificacion.leida) "Leída" else "Sin leer"
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = if (notificacion.leida) esquema.surfaceContainerHigh else esquema.primary,
                contentColor = if (notificacion.leida) {
                    esquema.onSurfaceVariant
                } else {
                    esquema.onPrimary
                },
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (notificacion.leida) "Leída" else "Sin leer",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
            Text(text = notificacion.titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = notificacion.fechaHora.format(formatoFechaNotificacion),
                style = MaterialTheme.typography.labelMedium,
                color = esquema.onSurfaceVariant
            )
            Text(text = notificacion.mensaje, style = MaterialTheme.typography.bodyMedium)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (notificacion.pedidoId != null) {
                    BotonSecundario(
                        texto = "Ver pedido #${notificacion.pedidoId}",
                        onClick = {
                            onMarcarLeida()
                            onVerPedido()
                        },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(Modifier.weight(1f))
                }
                IconButton(onClick = onEliminar, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Eliminar notificación",
                        tint = esquema.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(name = "Notificación sin leer", showBackground = true, widthDp = 360)
@Preview(name = "Notificación con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun TarjetaNotificacionPreview() {
    RescatAppTheme {
        TarjetaNotificacion(
            notificacion = Notificacion(
                titulo = "Tu pack está listo para retirar",
                mensaje = "El negocio terminó de preparar tu pedido. " +
                    "Muestra tu código al retirarlo.",
                fechaHora = LocalDateTime.of(2026, 11, 17, 18, 30),
                tipo = TipoNotificacion.PEDIDO_LISTO,
                rolDestino = RolUsuario.CLIENTE,
                pedidoId = 12
            ),
            onMarcarLeida = {},
            onEliminar = {},
            onVerPedido = {}
        )
    }
}
