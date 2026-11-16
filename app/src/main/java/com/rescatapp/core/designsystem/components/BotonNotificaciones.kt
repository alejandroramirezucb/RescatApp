package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BotonNotificaciones(conteoNoLeidas: Int, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
    ) {
        BadgedBox(badge = { InsigniaConteo(conteoNoLeidas) }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
        }
    }
}

@Composable
fun InsigniaConteo(conteo: Int) {
    if (conteo > 0) {
        Badge { Text(if (conteo > 99) "99+" else conteo.toString()) }
    }
}
