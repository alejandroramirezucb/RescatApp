package com.rescatapp.features.notificaciones.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.TipoNotificacion
import java.time.format.DateTimeFormatter

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
                        if (estado.conteoNoLeidas > 0) {
                            Spacer(Modifier.width(8.dp))
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    val textoConteo = if (estado.conteoNoLeidas > 99) {
                                        "99+"
                                    } else {
                                        estado.conteoNoLeidas.toString()
                                    }
                                    Text(
                                        text = textoConteo,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (estado.notificaciones.isNotEmpty()) {
                        if (estado.conteoNoLeidas > 0) {
                            IconButton(onClick = viewModel::marcarTodasComoLeidas) {
                                Icon(
                                    Icons.Default.DoneAll,
                                    contentDescription = "Marcar todas como leídas"
                                )
                            }
                        }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(espacioInterno),
                contentAlignment = Alignment.Center
            ) {
                EstadoVacio(
                    mensaje = "No tienes notificaciones en este momento.\n" +
                        "Aquí verás las alertas sobre pedidos y ofertas."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(espacioInterno),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
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

@Composable
private fun TarjetaNotificacion(
    notificacion: Notificacion,
    onMarcarLeida: () -> Unit,
    onEliminar: () -> Unit,
    onVerPedido: () -> Unit
) {
    val (icono, colorIcono) = obtenerIconoYColor(notificacion.tipo)
    val formatoHora = DateTimeFormatter.ofPattern("HH:mm")
    val horaTexto = runCatching { notificacion.fechaHora.format(formatoHora) }.getOrDefault("")

    val colorFondo = if (!notificacion.leida) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMarcarLeida() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondo),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (!notificacion.leida) 2.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorIcono.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notificacion.titulo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (!notificacion.leida) {
                            FontWeight.Bold
                        } else {
                            FontWeight.SemiBold
                        },
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (horaTexto.isNotBlank()) {
                        Text(
                            text = horaTexto,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = notificacion.mensaje,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (notificacion.pedidoId != null) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = {
                            onMarcarLeida()
                            onVerPedido()
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Ver pedido #${notificacion.pedidoId}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            IconButton(
                onClick = onEliminar,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun obtenerIconoYColor(tipo: TipoNotificacion): Pair<ImageVector, Color> = when (tipo) {
    TipoNotificacion.PEDIDO_EN_PREPARACION ->
        Icons.Default.Restaurant to MaterialTheme.colorScheme.primary

    TipoNotificacion.PEDIDO_LISTO -> Icons.Default.ShoppingBag to Color(0xFF2E7D32)

    TipoNotificacion.PEDIDO_RECOGIDO -> Icons.Default.CheckCircle to Color(0xFF2E7D32)

    TipoNotificacion.PEDIDO_CANCELADO -> Icons.Default.Cancel to MaterialTheme.colorScheme.error

    TipoNotificacion.NUEVO_PEDIDO_VENDEDOR ->
        Icons.Default.NotificationsActive to MaterialTheme.colorScheme.primary

    TipoNotificacion.OFERTA_AGOTADA -> Icons.Default.Inventory2 to Color(0xFFE65100)

    TipoNotificacion.OFERTA_PUBLICADA ->
        Icons.Default.Campaign to
            MaterialTheme.colorScheme.tertiary

    TipoNotificacion.PEDIDO_RESERVADO ->
        Icons.Default.BookmarkAdded to MaterialTheme.colorScheme.primary

    TipoNotificacion.SISTEMA -> Icons.Default.Notifications to MaterialTheme.colorScheme.primary
}
