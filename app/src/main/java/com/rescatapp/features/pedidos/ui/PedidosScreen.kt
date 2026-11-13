package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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

    var pedidoParaConfirmarRetiro by remember { mutableStateOf<Pedido?>(null) }
    var pedidoParaCancelar by remember { mutableStateOf<Pedido?>(null) }

    pedidoParaConfirmarRetiro?.let { pedido ->
        DialogoConfirmarRetiro(
            pedido = pedido,
            onConfirmar = {
                pedidoParaConfirmarRetiro = null
                viewModel.avanzar(pedido.id)
            },
            onDescartar = { pedidoParaConfirmarRetiro = null }
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

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Mis pedidos",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
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
                        TarjetaPedido(it)
                    }
                }
            }
        }
        SnackbarHost(estadoSnackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun TarjetaPedidoActivo(
    activo: PedidoConProgreso,
    esNegocio: Boolean,
    onAvanzar: (Pedido) -> Unit,
    onCancelar: (Pedido) -> Unit
) {
    val pedido = activo.pedido
    TarjetaPedido(pedido) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        FilaCodigoRetiro(pedido)
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
