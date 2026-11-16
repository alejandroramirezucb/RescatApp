package com.rescatapp.features.notificaciones.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.InsigniaConteo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(
    onVolver: () -> Unit,
    onVerPedidos: () -> Unit,
    viewModel: NotificacionesViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notificaciones")
                        Spacer(Modifier.width(8.dp))
                        InsigniaConteo(estado.conteoNoLeidas)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (estado.conteoNoLeidas > 0) {
                        IconButton(onClick = viewModel::marcarTodasComoLeidas) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Marcar como leídas")
                        }
                    }
                    if (estado.notificaciones.isNotEmpty()) {
                        IconButton(onClick = viewModel::limpiarTodas) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Limpiar historial"
                            )
                        }
                    }
                }
            )
        }
    ) { espacioInterno ->
        if (estado.notificaciones.isEmpty()) {
            EstadoVacio(
                mensaje = "No tienes notificaciones en este momento.\n" +
                    "Aquí verás las alertas sobre pedidos y ofertas.",
                modifier = Modifier.padding(espacioInterno)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(espacioInterno),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(estado.notificaciones, key = { it.id }) { notificacion ->
                    TarjetaNotificacion(
                        notificacion = notificacion,
                        onMarcarLeida = { viewModel.marcarComoLeida(notificacion.id) },
                        onEliminar = { viewModel.eliminar(notificacion.id) },
                        onVerPedido = onVerPedidos
                    )
                }
            }
        }
    }
}
