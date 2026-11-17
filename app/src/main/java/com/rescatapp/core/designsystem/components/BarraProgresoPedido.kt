package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.PasoProgreso

@Composable
fun BarraProgresoPedido(pasos: List<PasoProgreso>, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth < 300.dp || LocalDensity.current.fontScale > 1.2f) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                pasos.forEach { paso ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CirculoPaso(paso)
                        EtiquetaPaso(paso)
                    }
                }
            }
        } else {
            Row {
                pasos.forEachIndexed { indice, paso ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LineaProgreso(visible = indice > 0, completada = paso.completado)
                            CirculoPaso(paso)
                            LineaProgreso(
                                visible = indice < pasos.lastIndex,
                                completada = pasos.getOrNull(indice + 1)?.completado == true
                            )
                        }
                        EtiquetaPaso(paso)
                    }
                }
            }
        }
    }
}

@Composable
private fun EtiquetaPaso(paso: PasoProgreso) {
    Text(
        text = paso.etiqueta,
        style = MaterialTheme.typography.labelSmall,
        textAlign = TextAlign.Center,
        color = if (paso.completado || paso.actual) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        }
    )
}

@Composable
private fun RowScope.LineaProgreso(visible: Boolean, completada: Boolean) {
    val color = when {
        !visible -> Color.Transparent
        completada -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    Box(modifier = Modifier.weight(1f).height(2.dp).background(color))
}

@Composable
private fun CirculoPaso(paso: PasoProgreso) {
    val esquema = MaterialTheme.colorScheme
    val resaltado = paso.completado || paso.actual
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                if (resaltado) esquema.primary else esquema.surfaceContainerHigh,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        when {
            paso.actual -> Box(Modifier.size(8.dp).background(esquema.onPrimary, CircleShape))

            paso.completado -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = esquema.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview(name = "Progreso de pedido", showBackground = true, widthDp = 360)
@Preview(name = "Progreso con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun BarraProgresoPedidoPreview() {
    RescatAppTheme {
        BarraProgresoPedido(
            pasos = listOf(
                PasoProgreso("Reservado", completado = true, actual = false),
                PasoProgreso("Preparando", completado = true, actual = true),
                PasoProgreso("Listo", completado = false, actual = false),
                PasoProgreso("Recogido", completado = false, actual = false)
            )
        )
    }
}
