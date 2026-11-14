package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja

@Composable
internal fun EncabezadoNegocio(
    nombre: String,
    conteoNoLeidas: Int = 0,
    onVerNotificaciones: () -> Unit = {}
) {
    Column {
        Box(
            modifier = Modifier.fillMaxWidth().height(24.dp).background(Naranja)
        ) {
            Text(
                text = nombre,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hoy es un buen día",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Buenos días,\n$nombre",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            IconButton(
                onClick = onVerNotificaciones,
                modifier = Modifier
                    .size(44.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
            ) {
                BadgedBox(
                    badge = {
                        if (conteoNoLeidas > 0) {
                            Badge {
                                Text(if (conteoNoLeidas > 99) "99+" else conteoNoLeidas.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notificaciones",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
internal fun ResumenNegocio(estado: InicioNegocioUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Resumen de hoy",
            style = MaterialTheme.typography.titleMedium
        )
        FilaEstadisticasNegocio(
            TarjetaEstadisticaInfo(
                Icons.Filled.Paid,
                estado.ventasHoy,
                "Ventas",
                Color(0xFFFFF8F0)
            ),
            TarjetaEstadisticaInfo(
                Icons.Filled.ShoppingBag,
                estado.cantidadPedidos.toString(),
                "Pedidos",
                Color(0xFFFFF8F0)
            )
        )
        FilaEstadisticasNegocio(
            TarjetaEstadisticaInfo(
                Icons.Filled.CardGiftcard,
                estado.ofertasActivas.toString(),
                "Ofertas activas",
                Color(0xFFF0F8EC)
            ),
            TarjetaEstadisticaInfo(
                Icons.Filled.Spa,
                estado.rescatados.toString(),
                "Rescatados",
                Color(0xFFF0F8EC)
            )
        )
    }
}

private data class TarjetaEstadisticaInfo(
    val icono: ImageVector,
    val valor: String,
    val etiqueta: String,
    val colorFondo: Color
)

@Composable
private fun FilaEstadisticasNegocio(
    primera: TarjetaEstadisticaInfo,
    segunda: TarjetaEstadisticaInfo
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        TarjetaEstadisticaNegocio(primera, Modifier.weight(1f))
        TarjetaEstadisticaNegocio(segunda, Modifier.weight(1f))
    }
}

@Composable
private fun TarjetaEstadisticaNegocio(info: TarjetaEstadisticaInfo, modifier: Modifier) {
    Surface(color = info.colorFondo, shape = RoundedCornerShape(12.dp), modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(info.icono, contentDescription = null, tint = Naranja)
            Text(
                text = info.valor,
                style = MaterialTheme.typography.titleLarge,
                color = Naranja
            )
            Text(
                text = info.etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
