package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.designsystem.theme.bordeTarjeta
import com.rescatapp.core.util.formatearDinero

private val diasSemanaVentas = listOf(
    "Lun" to "Lunes",
    "Mar" to "Martes",
    "Mié" to "Miércoles",
    "Jue" to "Jueves",
    "Vie" to "Viernes",
    "Sáb" to "Sábado",
    "Dom" to "Domingo"
)

@Composable
internal fun VentasSemanaNegocio(ventasSemana: List<Double>, indiceDiaActual: Int) {
    val ventaMaxima = (ventasSemana.maxOrNull() ?: 0.0).coerceAtLeast(1.0)
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        border = bordeTarjeta(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = "Ventas semanales", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = ventasSemana.sum().formatearDinero(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Últimos 7 días · agrupados por día",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                diasSemanaVentas.forEachIndexed { indice, (abreviatura, nombre) ->
                    BarraDiaVenta(
                        dia = abreviatura,
                        nombre = nombre,
                        venta = ventasSemana.getOrElse(indice) { 0.0 },
                        ventaMaxima = ventaMaxima,
                        esHoy = indice == indiceDiaActual,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Text(
                text = if (ventasSemana.all { it == 0.0 }) {
                    "Sin ventas registradas en los últimos 7 días."
                } else {
                    "El día actual aparece resaltado."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun BarraDiaVenta(
    dia: String,
    nombre: String,
    venta: Double,
    ventaMaxima: Double,
    esHoy: Boolean,
    modifier: Modifier = Modifier
) {
    val esquema = MaterialTheme.colorScheme
    val proporcion = (venta / ventaMaxima).toFloat().coerceIn(0f, 1f)
    Column(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = "$nombre: ${venta.formatearDinero()}${if (esHoy) ", hoy" else ""}"
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(96.dp)
                .clip(MaterialTheme.shapes.small)
                .background(esquema.surfaceContainerLow),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp * proporcion)
                    .background(if (esHoy) esquema.primary else esquema.secondary)
            )
        }
        Surface(
            color = if (esHoy) esquema.primaryContainer else esquema.surface,
            shape = MaterialTheme.shapes.small
        ) {
            Text(
                text = dia,
                modifier = Modifier.padding(horizontal = 2.dp, vertical = 4.dp),
                softWrap = false,
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp),
                color = if (esHoy) esquema.onPrimaryContainer else esquema.onSurfaceVariant,
                fontWeight = if (esHoy) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Preview(name = "Ventas del negocio", showBackground = true, widthDp = 360)
@Preview(name = "Ventas con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun VentasSemanaNegocioPreview() {
    RescatAppTheme {
        VentasSemanaNegocio(listOf(50.0, 80.0, 35.0, 0.0, 100.0, 60.0, 0.0), 4)
    }
}
