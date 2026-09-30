package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.util.formatearDinero

@Composable
internal fun PedidosRecientesNegocio(pedidos: List<Pedido>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Pedidos recientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (pedidos.isEmpty()) {
            EstadoSinPedidos()
        } else {
            pedidos.forEach { TarjetaPedidoRecienteNegocio(it) }
        }
    }
}

@Composable
private fun EstadoSinPedidos() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Aún no hay pedidos registrados para este negocio.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun TarjetaPedidoRecienteNegocio(pedido: Pedido) {
    val colorEstado = colorEstado(pedido.estado)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DatosPedidoReciente(pedido, colorEstado)
            Text(
                text = pedido.precioPagado.formatearDinero(),
                style = MaterialTheme.typography.titleMedium,
                color = Naranja,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DatosPedidoReciente(pedido: Pedido, colorEstado: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "#APV-100${pedido.id} · ${pedido.nombreOferta}",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        DetallePedidoReciente(pedido.horaRetiroDesde)
        Spacer(Modifier.size(0.dp, 4.dp))
        EstadoPedidoReciente(pedido.estado.etiqueta, colorEstado)
    }
}

@Composable
private fun DetallePedidoReciente(hora: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            Icons.Default.Person,
            null,
            Modifier.size(12.dp),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Cliente",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "·",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Icon(
            Icons.Default.Schedule,
            null,
            Modifier.size(12.dp),
            MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            hora,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
