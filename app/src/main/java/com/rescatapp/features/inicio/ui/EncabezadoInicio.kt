package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.AccesoBusqueda

@Composable
internal fun EncabezadoInicio(
    usuario: String,
    onBuscar: () -> Unit,
    conteoNoLeidas: Int = 0,
    onVerNotificaciones: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = margenLateral),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                UbicacionActual()
                Text(text = "¡Hola, $usuario!", style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = "¿Qué vamos a aprovechar hoy?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IndicadorNotificaciones(
                conteoNoLeidas = conteoNoLeidas,
                onClick = onVerNotificaciones
            )
        }
        AccesoBusqueda(onClick = onBuscar)
    }
}

@Composable
private fun UbicacionActual() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Santa Cruz de la Sierra",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun IndicadorNotificaciones(conteoNoLeidas: Int, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
    ) {
        BadgedBox(
            badge = {
                if (conteoNoLeidas > 0) {
                    Badge {
                        Text(if (conteoNoLeidas > 99) "99+" else conteoNoLeidas.toString())
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notificaciones",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
