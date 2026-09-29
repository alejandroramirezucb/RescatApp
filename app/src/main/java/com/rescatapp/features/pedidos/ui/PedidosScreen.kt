package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BarraProgresoPedido
import com.rescatapp.core.designsystem.components.ChipSeleccionable
import com.rescatapp.core.designsystem.components.EfectoMensajeTemporal
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.TarjetaPedido
import com.rescatapp.core.model.EstadoPedido

@Composable
fun PedidosScreen(viewModel: PedidosViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val estadoSnackbar = remember { SnackbarHostState() }
    EfectoMensajeTemporal(estado.mensaje, estadoSnackbar, viewModel::limpiarMensaje)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "Mis pedidos",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(vertical = 20.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PestanaPedidos.entries.forEach { pestana ->
                    ChipSeleccionable(
                        etiqueta = pestana.titulo,
                        seleccionado = estado.pestana == pestana,
                        onClick = { viewModel.seleccionarPestana(pestana) }
                    )
                }
            }
            ListaPedidos(
                estado = estado,
                onAvanzar = viewModel::avanzar,
                onCancelar = viewModel::cancelar
            )
        }
        SnackbarHost(estadoSnackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun ListaPedidos(
    estado: PedidosUiState,
    onAvanzar: (Int) -> Unit,
    onCancelar: (Int) -> Unit
) {
    if (estado.pestanaSinPedidos) {
        EstadoVacio(estado.pestana.mensajeSinPedidos)
        return
    }
    LazyColumn(
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (estado.pestana) {
            PestanaPedidos.ACTIVOS -> items(estado.activos, key = { it.pedido.id }) { activo ->
                TarjetaPedido(activo.pedido) {
                    HorizontalDivider()
                    BarraProgresoPedido(activo.pasos, modifier = Modifier.padding(12.dp))
                    AccionesPedido(
                        estado = activo.pedido.estado,
                        onAvanzar = { onAvanzar(activo.pedido.id) },
                        onCancelar = { onCancelar(activo.pedido.id) }
                    )
                }
            }

            PestanaPedidos.HISTORIAL -> items(estado.historial, key = { it.id }) { pedido ->
                TarjetaPedido(pedido)
            }
        }
    }
}

@Composable
private fun AccionesPedido(estado: EstadoPedido, onAvanzar: () -> Unit, onCancelar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
    ) {
        if (estado == EstadoPedido.RESERVADO) {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = MaterialTheme.colorScheme.error)
            }
        }
        estado.textoParaAvanzar?.let { texto ->
            Button(onClick = onAvanzar) { Text(texto) }
        }
    }
}

private val EstadoPedido.textoParaAvanzar: String?
    get() = when (this) {
        EstadoPedido.RESERVADO -> "Marcar como Preparando"
        EstadoPedido.PREPARANDO -> "Marcar como Listo"
        EstadoPedido.LISTO -> "Confirmar retiro"
        EstadoPedido.RECOGIDO, EstadoPedido.CANCELADO -> null
    }
