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
import com.rescatapp.core.designsystem.components.EfectoMensajeTemporal
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.TarjetaPedido
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido

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
            SelectorPestanasPedidos(estado.pestana, viewModel::seleccionarPestana)
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
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    BarraProgresoPedido(activo.pasos, modifier = Modifier.padding(16.dp))

                    if (estado.esNegocio) {
                        AccionesPedidoNegocio(
                            pedido = activo.pedido,
                            onAvanzar = { onAvanzar(activo.pedido.id) },
                            onCancelar = { onCancelar(activo.pedido.id) }
                        )
                    }
                }
            }

            PestanaPedidos.HISTORIAL -> items(estado.historial, key = { it.id }) { pedido ->
                TarjetaPedido(pedido)
            }
        }
    }
}

@Composable
private fun AccionesPedidoNegocio(pedido: Pedido, onAvanzar: () -> Unit, onCancelar: () -> Unit) {
    val textoBotonPrimario = when (pedido.estado) {
        EstadoPedido.RESERVADO -> "Comenzar a preparar"
        EstadoPedido.PREPARANDO -> "Marcar como listo"
        EstadoPedido.LISTO -> "Confirmar retiro"
        else -> null
    }

    if (textoBotonPrimario != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (pedido.estado == EstadoPedido.RESERVADO) {
                TextButton(
                    onClick = onCancelar,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar pedido", color = MaterialTheme.colorScheme.error)
                }
            }
            Button(
                onClick = onAvanzar,
                modifier = Modifier.weight(1f)
            ) {
                Text(textoBotonPrimario)
            }
        }
    }
}
