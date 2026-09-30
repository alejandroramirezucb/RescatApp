package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearPeso

@Composable
fun FilaImpacto(impacto: Impacto, colorValores: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        IndicadorImpacto(impacto.rescates.toString(), "Rescates", colorValores)
        VerticalDivider()
        IndicadorImpacto(impacto.ahorrado.formatearDinero(), "Ahorrado", colorValores)
        VerticalDivider()
        IndicadorImpacto(impacto.kgAprovechados.formatearPeso(), "Aprovechado", colorValores)
    }
}

/** Alias para compatibilidad con criterios de diseño */
@Composable
fun FilaEstadisticas(impacto: Impacto, colorValores: Color, modifier: Modifier = Modifier) {
    FilaImpacto(impacto = impacto, colorValores = colorValores, modifier = modifier)
}

@Composable
private fun RowScope.IndicadorImpacto(valor: String, etiqueta: String, colorValor: Color) {
    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = valor, style = MaterialTheme.typography.titleLarge, color = colorValor)
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(name = "Fila Estadísticas / Impacto", showBackground = true)
@Composable
private fun FilaEstadisticasPreview() {
    FilaEstadisticas(
        impacto = Impacto(rescates = 4, ahorrado = 102.0, kgAprovechados = 3.6),
        colorValores = Verde
    )
}
