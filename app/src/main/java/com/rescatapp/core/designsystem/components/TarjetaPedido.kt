package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.PasoProgreso
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearHorario

@Composable
fun TarjetaPedido(
    pedido: Pedido,
    modifier: Modifier = Modifier,
    contenidoInferior: @Composable ColumnScope.() -> Unit = {}
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.height(100.dp)) {
            ImagenCategoria(
                categoria = pedido.categoria,
                modifier = Modifier.width(100.dp).fillMaxHeight(),
                tamanoIcono = 32.dp
            )
            Column(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = pedido.nombreOferta,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = pedido.precioPagado.formatearDinero(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = pedido.comercio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (pedido.estaActivo) {
                    Text(
                        text = formatearHorario(pedido.horaRetiroDesde, pedido.horaRetiroHasta),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    ChipEstado(pedido.estado)
                }
            }
        }
        Column(modifier = Modifier.fillMaxWidth(), content = contenidoInferior)
    }
}

@Preview(name = "Tarjeta Pedido Activo", showBackground = true)
@Composable
private fun TarjetaPedidoActivoPreview() {
    val pedido = Pedido(
        id = 1,
        ofertaId = 14,
        nombreOferta = "Pack Sorpresa",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        precioPagado = 25.0,
        ahorro = 20.0,
        pesoKg = 1.0,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00",
        estado = EstadoPedido.PREPARANDO
    )
    TarjetaPedido(pedido = pedido) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        BarraProgresoPedido(
            pasos = listOf(
                PasoProgreso("Reservado", completado = true, actual = false),
                PasoProgreso("Preparando", completado = true, actual = true),
                PasoProgreso("Listo", completado = false, actual = false),
                PasoProgreso("Recogido", completado = false, actual = false)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Tarjeta Pedido Historial", showBackground = true)
@Composable
private fun TarjetaPedidoHistorialPreview() {
    val pedido = Pedido(
        id = 2,
        ofertaId = 14,
        nombreOferta = "Pack Sorpresa",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        precioPagado = 25.0,
        ahorro = 20.0,
        pesoKg = 1.0,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00",
        estado = EstadoPedido.RECOGIDO
    )
    TarjetaPedido(pedido = pedido)
}
