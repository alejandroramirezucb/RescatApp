package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido

@Composable
internal fun FilaCodigoRetiro(pedido: Pedido) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column {
                Text(
                    text = "CÓDIGO DE RESCATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = pedido.codigoRescate,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "Mostrar al retirar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BotonPrincipal(texto = textoAvanzar, onClick = onAvanzar)
        if (pedido.estado == EstadoPedido.RESERVADO) {
            TextButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar pedido", color = MaterialTheme.colorScheme.error)
            }
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
