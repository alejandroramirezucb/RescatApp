package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.ImagenCategoria
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.util.formatearDinero

@Composable
internal fun TarjetaPedidoHistorial(pedido: Pedido) {
    val textoGrande = LocalDensity.current.fontScale > 1.2f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min).heightIn(min = 112.dp)) {
            ImagenCategoria(
                categoria = pedido.categoria,
                imagenUri = pedido.imagenUri,
                modifier = Modifier.width(if (textoGrande) 72.dp else 88.dp).fillMaxHeight()
            )
            Column(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (textoGrande) {
                    NombrePedidoHistorial(pedido, Modifier.fillMaxWidth())
                    PrecioPedidoHistorial(pedido)
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NombrePedidoHistorial(pedido, Modifier.weight(1f))
                        PrecioPedidoHistorial(pedido)
                    }
                }
                Text(
                    text = pedido.comercio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                EstadoHistorial(pedido.estado)
            }
        }
    }
}

@Composable
private fun NombrePedidoHistorial(pedido: Pedido, modifier: Modifier) {
    Text(
        text = pedido.nombreOferta,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PrecioPedidoHistorial(pedido: Pedido) {
    Text(
        text = pedido.precioPagado.formatearDinero(),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview(name = "Historial compacto", showBackground = true, widthDp = 390)
@Preview(name = "Historial con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun TarjetaPedidoHistorialPreview() {
    val ejemplo = Pedido(
        id = 4,
        ofertaId = 1,
        nombreOferta = "Pack Café+",
        comercio = "Café Aroma",
        categoria = Categoria.CAFETERIA,
        precioPagado = 18.0,
        ahorro = 17.0,
        pesoKg = 0.5,
        horaRetiroDesde = "20:00",
        horaRetiroHasta = "22:00",
        estado = EstadoPedido.RECOGIDO
    )
    RescatAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            TarjetaPedidoHistorial(ejemplo)
            TarjetaPedidoHistorial(
                ejemplo.copy(
                    id = 5,
                    nombreOferta = "Sándwich Combo",
                    estado = EstadoPedido.CANCELADO
                )
            )
            FilaCodigoRetiro(ejemplo)
        }
    }
}
