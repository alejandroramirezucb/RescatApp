package com.rescatapp.features.notificaciones.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BarraSuperior
import com.rescatapp.core.designsystem.theme.RescatAppTheme

@Composable
fun NotificacionesScreen(
    onVolver: () -> Unit,
    onVerPedidos: () -> Unit,
    viewModel: NotificacionesViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    NotificacionesContenido(
        estado = estado,
        onVolver = onVolver,
        onVerPedidos = onVerPedidos,
        onMarcarTodasLeidas = viewModel::marcarTodasComoLeidas,
        onLimpiarHistorial = viewModel::limpiarTodas,
        onMarcarLeida = viewModel::marcarComoLeida,
        onEliminar = viewModel::eliminar
    )
}

@Composable
internal fun NotificacionesContenido(
    estado: NotificacionesUiState,
    onVolver: () -> Unit,
    onVerPedidos: () -> Unit,
    onMarcarTodasLeidas: () -> Unit,
    onLimpiarHistorial: () -> Unit,
    onMarcarLeida: (String) -> Unit,
    onEliminar: (String) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { BarraSuperior(titulo = "Notificaciones", onVolver = onVolver) }
    ) { espacioInterno ->
        Box(
            modifier = Modifier.fillMaxSize().padding(espacioInterno),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier.widthIn(max = 600.dp).fillMaxSize(),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    top = 24.dp,
                    end = 20.dp,
                    bottom = 48.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (estado.notificaciones.isEmpty()) {
                    item { SinNotificaciones() }
                } else {
                    item {
                        ResumenNotificaciones(
                            conteoNoLeidas = estado.conteoNoLeidas,
                            onMarcarTodasLeidas = onMarcarTodasLeidas,
                            onLimpiarHistorial = onLimpiarHistorial
                        )
                    }
                    items(estado.notificaciones, key = { it.id }) { notificacion ->
                        TarjetaNotificacion(
                            notificacion = notificacion,
                            onMarcarLeida = { onMarcarLeida(notificacion.id) },
                            onEliminar = { onEliminar(notificacion.id) },
                            onVerPedido = onVerPedidos
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SinNotificaciones() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsNone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
            Text(text = "Todo tranquilo por aquí", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "No tienes notificaciones en este momento. " +
                    "Aquí verás los avisos sobre pedidos y ofertas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "Notificaciones vacías", showBackground = true, widthDp = 360, heightDp = 640)
@Preview(name = "Notificaciones con texto grande", widthDp = 320, heightDp = 640, fontScale = 1.5f)
@Composable
private fun NotificacionesContenidoPreview() {
    RescatAppTheme {
        NotificacionesContenido(
            estado = NotificacionesUiState(),
            onVolver = {},
            onVerPedidos = {},
            onMarcarTodasLeidas = {},
            onLimpiarHistorial = {},
            onMarcarLeida = {},
            onEliminar = {}
        )
    }
}
