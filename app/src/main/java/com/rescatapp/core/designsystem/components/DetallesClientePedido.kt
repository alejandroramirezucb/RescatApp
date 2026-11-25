package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Pedido

@Composable
fun DetallesClientePedido(pedido: Pedido, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Cliente comprador",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = pedido.clienteNombre ?: "Nombre no disponible",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = pedido.clienteTelefono?.let { "Teléfono: $it" } ?: "Teléfono no disponible",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
