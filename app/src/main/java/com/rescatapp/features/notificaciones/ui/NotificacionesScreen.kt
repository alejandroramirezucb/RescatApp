package com.rescatapp.features.notificaciones.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.AvisoTemporal
import com.rescatapp.core.designsystem.components.BarraSuperior

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
        onEliminar = viewModel::eliminar,
        onErrorMostrado = viewModel::limpiarError
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
    onEliminar: (String) -> Unit,
    onErrorMostrado: () -> Unit = {}
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { BarraSuperior(titulo = "Notificaciones", onVolver = onVolver) },
        snackbarHost = { AvisoTemporal(estado.error, onErrorMostrado) }
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
