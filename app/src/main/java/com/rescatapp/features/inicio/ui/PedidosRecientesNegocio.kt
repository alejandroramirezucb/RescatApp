package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.TarjetaPedido
import com.rescatapp.core.model.Pedido

@Composable
internal fun PedidosRecientesNegocio(pedidos: List<Pedido>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Pedidos recientes", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Consulta los packs y el estado de cada pedido.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (pedidos.isEmpty()) {
            EstadoSinPedidos()
        } else {
            pedidos.forEach { pedido ->
                TarjetaPedido(pedido) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Text(
                        text = "Código de rescate: ${pedido.codigoRescate}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EstadoSinPedidos() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tu próximo rescate empieza aquí",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Aún no hay pedidos registrados para este negocio.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
