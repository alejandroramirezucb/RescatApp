package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BarraProgresoPedido
import com.rescatapp.core.designsystem.components.TarjetaPedido
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido

@Composable
internal fun TarjetaPedidoActivo(
    activo: PedidoConProgreso,
    esNegocio: Boolean,
    onAvanzar: (Pedido) -> Unit,
    onCancelar: (Pedido) -> Unit
) {
    val pedido = activo.pedido
    TarjetaPedido(pedido) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        if (!esNegocio) {
            FilaCodigoRetiro(pedido)
        }
        BarraProgresoPedido(activo.pasos, modifier = Modifier.padding(16.dp))
        if (esNegocio) {
            AccionesPedidoNegocio(
                pedido = pedido,
                onAvanzar = { onAvanzar(pedido) },
                onCancelar = { onCancelar(pedido) }
            )
        } else if (pedido.estado == EstadoPedido.RESERVADO) {
            AccionCancelarReserva(onCancelar = { onCancelar(pedido) })
        }
    }
}
