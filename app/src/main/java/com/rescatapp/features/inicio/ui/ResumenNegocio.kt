package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BotonNotificaciones
import com.rescatapp.core.designsystem.theme.RescatAppTheme

@Composable
internal fun EncabezadoNegocio(
    nombre: String,
    conteoNoLeidas: Int,
    onVerNotificaciones: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp, top = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "TU NEGOCIO EN RESCATAPP",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = nombre, style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Cada pack es una nueva oportunidad de rescate.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        BotonNotificaciones(conteoNoLeidas, onVerNotificaciones)
    }
}

@Composable
internal fun ResumenNegocio(estado: InicioNegocioUiState) {
    val esquema = MaterialTheme.colorScheme
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "Tu negocio en cifras", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "Un resumen de tus ventas, pedidos y packs.",
            style = MaterialTheme.typography.bodySmall,
            color = esquema.onSurfaceVariant
        )
        FilaEstadisticasNegocio(
            TarjetaEstadisticaInfo(
                Icons.Outlined.Paid,
                estado.ventasHoy,
                "Ventas acumuladas",
                esquema.primaryContainer,
                esquema.onPrimaryContainer
            ),
            TarjetaEstadisticaInfo(
                Icons.Outlined.ShoppingBag,
                estado.cantidadPedidos.toString(),
                "Pedidos registrados",
                esquema.primaryContainer,
                esquema.onPrimaryContainer
            )
        )
        FilaEstadisticasNegocio(
            TarjetaEstadisticaInfo(
                Icons.Outlined.CardGiftcard,
                estado.ofertasActivas.toString(),
                "Ofertas activas",
                esquema.secondaryContainer,
                esquema.onSecondaryContainer
            ),
            TarjetaEstadisticaInfo(
                Icons.Outlined.Spa,
                estado.rescatados.toString(),
                "Listos o recogidos",
                esquema.secondaryContainer,
                esquema.onSecondaryContainer
            )
        )
    }
}

@Preview(name = "Resumen del negocio", showBackground = true, widthDp = 400)
@Preview(name = "Resumen con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun ResumenNegocioPreview() {
    RescatAppTheme {
        ResumenNegocio(
            InicioNegocioUiState(
                ventasHoy = "Bs. 1.250,00",
                cantidadPedidos = 24,
                ofertasActivas = 8,
                rescatados = 17
            )
        )
    }
}
