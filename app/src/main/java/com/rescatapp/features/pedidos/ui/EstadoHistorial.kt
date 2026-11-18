package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.EstadoPedido

@Composable
internal fun EstadoHistorial(estado: EstadoPedido) {
    val cancelado = estado == EstadoPedido.CANCELADO
    val esquema = MaterialTheme.colorScheme
    Surface(
        color = if (cancelado) esquema.errorContainer else esquema.secondaryContainer,
        contentColor = if (cancelado) esquema.onErrorContainer else esquema.onSecondaryContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = if (cancelado) Icons.Outlined.Cancel else Icons.Outlined.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(text = estado.etiquetaHistorial, style = MaterialTheme.typography.labelLarge)
        }
    }
}
