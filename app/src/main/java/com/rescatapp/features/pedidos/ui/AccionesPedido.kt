package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.NaranjaSuave
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido

@Composable
internal fun FilaCodigoRetiro(pedido: Pedido) {
    Surface(
        color = NaranjaSuave.copy(alpha = 0.45f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "CÓDIGO DE RESCATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = pedido.codigoRescate,
                    style = MaterialTheme.typography.titleMedium,
                    color = Naranja
                )
            }
            Text(
                text = "Mostrar al retirar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun AccionesPedidoNegocio(pedido: Pedido, onAvanzar: () -> Unit, onCancelar: () -> Unit) {
    val textoAvanzar = when (pedido.estado) {
        EstadoPedido.RESERVADO -> "Comenzar a preparar"
        EstadoPedido.PREPARANDO -> "Marcar como listo"
        EstadoPedido.LISTO -> "Confirmar retiro"
        else -> return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (pedido.estado == EstadoPedido.RESERVADO) {
            TextButton(onClick = onCancelar, modifier = Modifier.weight(1f)) {
                Text("Cancelar pedido", color = MaterialTheme.colorScheme.error)
            }
        }
        Button(onClick = onAvanzar, modifier = Modifier.weight(1f)) {
            Text(textoAvanzar)
        }
    }
}

@Composable
internal fun AccionCancelarReserva(onCancelar: () -> Unit) {
    TextButton(
        onClick = onCancelar,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Text("Cancelar reserva", color = MaterialTheme.colorScheme.error)
    }
}
