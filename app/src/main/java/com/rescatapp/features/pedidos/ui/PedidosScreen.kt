package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BarraProgresoPedido
import com.rescatapp.core.designsystem.components.EfectoMensajeTemporal
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.TarjetaPedido
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.NaranjaSuave
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido

@Composable
fun PedidosScreen(viewModel: PedidosViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val estadoSnackbar = remember { SnackbarHostState() }
    EfectoMensajeTemporal(estado.mensaje, estadoSnackbar, viewModel::limpiarMensaje)

    var pedidoParaConfirmarRetiro by remember { mutableStateOf<Pedido?>(null) }
    var pedidoParaCancelar by remember { mutableStateOf<Pedido?>(null) }

    pedidoParaConfirmarRetiro?.let { pedido ->
        AlertDialog(
            onDismissRequest = { pedidoParaConfirmarRetiro = null },
            title = {
                Text(
                    text = "Confirmar retiro de pedido",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "¿Confirmas la entrega del pack \"${pedido.nombreOferta}\"?",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Código de rescate: #APV-100${pedido.id}\n" +
                            "Comprueba que coincida con el que te muestra el cliente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = pedido.id
                        pedidoParaConfirmarRetiro = null
                        viewModel.avanzar(id)
                    }
                ) {
                    Text("Confirmar entrega")
                }
            },
            dismissButton = {
                TextButton(onClick = { pedidoParaConfirmarRetiro = null }) {
                    Text("Volver")
                }
            }
        )
    }

    pedidoParaCancelar?.let { pedido ->
        AlertDialog(
            onDismissRequest = { pedidoParaCancelar = null },
            title = {
                Text(
                    text = "Cancelar pedido",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de cancelar el pedido #APV-100${pedido.id}? " +
                        "La unidad se devolverá al inventario disponible de la oferta.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = pedido.id
                        pedidoParaCancelar = null
                        viewModel.cancelar(id)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Sí, cancelar")
                }
            },
            dismissButton = {
                TextButton(onClick = { pedidoParaCancelar = null }) {
                    Text("Volver")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Mis pedidos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            item {
                SelectorPestanasPedidos(estado.pestana, viewModel::seleccionarPestana)
            }
            if (estado.pestanaSinPedidos) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EstadoVacio(estado.pestana.mensajeSinPedidos)
                    }
                }
            } else {
                when (estado.pestana) {
                    PestanaPedidos.ACTIVOS -> items(estado.activos, key = {
                        it.pedido.id
                    }) { activo ->
                        TarjetaPedido(activo.pedido) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                            FilaCodigoRetiro(activo.pedido)

                            BarraProgresoPedido(activo.pasos, modifier = Modifier.padding(16.dp))

                            if (estado.esNegocio) {
                                AccionesPedidoNegocio(
                                    pedido = activo.pedido,
                                    onAvanzar = {
                                        if (activo.pedido.estado == EstadoPedido.LISTO) {
                                            pedidoParaConfirmarRetiro = activo.pedido
                                        } else {
                                            viewModel.avanzar(activo.pedido.id)
                                        }
                                    },
                                    onCancelar = { pedidoParaCancelar = activo.pedido }
                                )
                            } else if (activo.pedido.estado == EstadoPedido.RESERVADO) {
                                TextButton(
                                    onClick = { pedidoParaCancelar = activo.pedido },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
                                ) {
                                    Text(
                                        "Cancelar reserva",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    PestanaPedidos.HISTORIAL -> items(estado.historial, key = { it.id }) { pedido ->
                        TarjetaPedido(pedido)
                    }
                }
            }
        }
        SnackbarHost(estadoSnackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun FilaCodigoRetiro(pedido: Pedido) {
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
                    text = "#APV-100${pedido.id}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
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
