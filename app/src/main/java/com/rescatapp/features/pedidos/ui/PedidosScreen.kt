package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.AvisoTemporal
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido

@Composable
fun PedidosScreen(viewModel: PedidosViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    var pedidoParaConfirmarRetiro by remember { mutableStateOf<Pedido?>(null) }
    var pedidoParaCancelar by remember { mutableStateOf<Pedido?>(null) }

    pedidoParaConfirmarRetiro?.let { pedido ->
        DialogoConfirmarRetiro(
            pedido = pedido,
            onConfirmar = {
                pedidoParaConfirmarRetiro = null
                viewModel.avanzar(pedido.id)
            },
            onDescartar = { pedidoParaConfirmarRetiro = null },
            onCodigoConfirmado = { codigo ->
                pedidoParaConfirmarRetiro = null
                viewModel.avanzar(pedido.id, codigo)
            }
        )
    }

    pedidoParaCancelar?.let { pedido ->
        DialogoCancelarPedido(
            pedido = pedido,
            onConfirmar = {
                pedidoParaCancelar = null
                viewModel.cancelar(pedido.id)
            },
            onDescartar = { pedidoParaCancelar = null }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier.align(Alignment.TopCenter).widthIn(max = 600.dp).fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Mis pedidos",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = if (estado.esNegocio) {
                            "Organiza la preparación y entrega de cada pack."
                        } else {
                            "Sigue tus rescates y consulta tus pedidos anteriores."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            item { SelectorPestanasPedidos(estado.pestana, viewModel::seleccionarPestana) }
            if (estado.pestanaSinPedidos) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        EstadoVacio(estado.pestana.mensajeSinPedidos)
                    }
                }
            } else {
                when (estado.pestana) {
                    PestanaPedidos.ACTIVOS -> items(estado.activos, key = { it.pedido.id }) {
                        TarjetaPedidoActivo(
                            activo = it,
                            esNegocio = estado.esNegocio,
                            onAvanzar = { pedido ->
                                if (pedido.estado == EstadoPedido.LISTO) {
                                    pedidoParaConfirmarRetiro = pedido
                                } else {
                                    viewModel.avanzar(pedido.id)
                                }
                            },
                            onCancelar = { pedido -> pedidoParaCancelar = pedido }
                        )
                    }

                    PestanaPedidos.HISTORIAL -> items(estado.historial, key = { it.id }) {
                        TarjetaPedidoHistorial(it)
                    }
                }
            }
        }
        AvisoTemporal(
            estado.mensaje,
            viewModel::limpiarMensaje,
            Modifier.align(Alignment.BottomCenter)
        )
    }
}
