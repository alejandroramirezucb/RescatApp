package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.FondoRescat
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearHorario

@Composable
fun PedidosScreen(viewModel: PedidosViewModel = hiltViewModel()) {
    val pedidos by viewModel.pedidos.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize().background(FondoRescat).padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Mis pedidos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
        )
        if (pedidos.isEmpty()) {
            Text("Aún no tienes pedidos", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(pedidos, key = { it.id }) { pedido ->
                    TarjetaPedido(pedido)
                }
            }
        }
    }
}

@Composable
private fun TarjetaPedido(pedido: Pedido) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                pedido.nombreOferta,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(pedido.comercio, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(formatearHorario(pedido.horaRetiroDesde, pedido.horaRetiroHasta))
            Text(pedido.precioPagado.formatearDinero(), color = NaranjaRescat)
            Text(pedido.estado.etiquetaHistorial, style = MaterialTheme.typography.labelLarge)
        }
    }
}
