package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.DialogoConfirmacion
import com.rescatapp.core.model.Pedido

@Composable
internal fun DialogoConfirmarRetiro(
    pedido: Pedido,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit
) {
    DialogoConfirmacion(
        titulo = "Confirmar retiro de pedido",
        textoConfirmar = "Confirmar entrega",
        onConfirmar = onConfirmar,
        onDescartar = onDescartar
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "¿Confirmas la entrega del pack \"${pedido.nombreOferta}\"?",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Código de rescate: ${pedido.codigoRescate}\n" +
                    "Comprueba que coincida con el que te muestra el cliente.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
internal fun DialogoCancelarPedido(
    pedido: Pedido,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit
) {
    DialogoConfirmacion(
        titulo = "Cancelar pedido",
        textoConfirmar = "Sí, cancelar",
        onConfirmar = onConfirmar,
        onDescartar = onDescartar,
        colorConfirmar = MaterialTheme.colorScheme.error
    ) {
        Text(
            text = "¿Estás seguro de cancelar el pedido ${pedido.codigoRescate}? " +
                "La unidad se devolverá al inventario disponible de la oferta.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
