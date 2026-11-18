package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
internal fun FilaEstadisticasNegocio(
    primera: TarjetaEstadisticaInfo,
    segunda: TarjetaEstadisticaInfo
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth < 320.dp || LocalDensity.current.fontScale > 1.2f) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaEstadisticaNegocio(primera, Modifier.fillMaxWidth())
                TarjetaEstadisticaNegocio(segunda, Modifier.fillMaxWidth())
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaEstadisticaNegocio(primera, Modifier.weight(1f))
                TarjetaEstadisticaNegocio(segunda, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun TarjetaEstadisticaNegocio(info: TarjetaEstadisticaInfo, modifier: Modifier) {
    Surface(color = info.colorFondo, shape = MaterialTheme.shapes.large, modifier = modifier) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = info.icono,
                contentDescription = null,
                tint = info.colorContenido,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = info.valor,
                style = MaterialTheme.typography.headlineSmall,
                color = info.colorContenido
            )
            Text(
                text = info.etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = info.colorContenido
            )
        }
    }
}

internal data class TarjetaEstadisticaInfo(
    val icono: ImageVector,
    val valor: String,
    val etiqueta: String,
    val colorFondo: Color,
    val colorContenido: Color
)
