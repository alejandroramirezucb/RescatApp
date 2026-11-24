package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.core.designsystem.components.DialogoConfirmacion
import com.rescatapp.core.model.Pedido

@Composable
internal fun DialogoConfirmarRetiro(
    pedido: Pedido,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit,
    onCodigoConfirmado: ((String) -> Unit)? = null
) {
    var codigo by remember { mutableStateOf("") }
    DialogoConfirmacion(
        titulo = "Confirmar retiro de pedido",
        textoConfirmar = "Confirmar entrega",
        icono = Icons.Outlined.TaskAlt,
        onConfirmar = {
            if (onCodigoConfirmado !=
                null
            ) {
                onCodigoConfirmado(codigo.trim())
            } else {
                onConfirmar()
            }
        },
        onDescartar = onDescartar
    ) {
        Text(pedido.nombreOferta, style = MaterialTheme.typography.titleSmall)
        CampoConError(
            etiqueta = "Codigo que muestra el cliente",
            valor = codigo,
            onValorCambiado = { codigo = it },
            ejemplo = "#APV-123"
        )
        Text(
            text = "Comprueba que el código coincida con el que te muestra el cliente.",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Al confirmar, el pedido quedará marcado como recogido.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
internal fun DialogoCancelarPedido(
    pedido: Pedido,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit,
    esNegocio: Boolean = false
) {
    DialogoConfirmacion(
        titulo = "Cancelar pedido",
        textoConfirmar = "Sí, cancelar",
        icono = Icons.Outlined.Cancel,
        onConfirmar = onConfirmar,
        onDescartar = onDescartar,
        colorConfirmar = MaterialTheme.colorScheme.error
    ) {
        ResumenPedidoDialogo(pedido, esNegocio)
        Text(
            text = "¿Estás seguro de cancelar este pedido? " +
                "La unidad se devolverá al inventario disponible de la oferta.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ResumenPedidoDialogo(pedido: Pedido, esNegocio: Boolean) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(text = pedido.nombreOferta, style = MaterialTheme.typography.titleSmall)
            Text(text = pedido.comercio, style = MaterialTheme.typography.bodySmall)
            Text(
                text = if (esNegocio) "Pedido #${pedido.id}" else pedido.codigoRescate,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
