package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearPeso

@Composable
internal fun BloqueImpacto(impacto: Impacto) {
    val indicadores = listOf(
        impacto.rescates.toString() to "Rescates",
        impacto.ahorrado.formatearDinero() to "Ahorrado",
        impacto.kgAprovechados.formatearPeso() to "Aprovechado"
    )
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth().padding(rellenoLateral)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Cada rescate cuenta",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "Tu resumen de rescates y ahorro.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth < 280.dp || LocalDensity.current.fontScale > 1.2f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        indicadores.forEach { (valor, etiqueta) ->
                            IndicadorImpactoInicio(valor, etiqueta)
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        indicadores.forEach { (valor, etiqueta) ->
                            IndicadorImpactoInicio(valor, etiqueta, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadorImpactoInicio(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = valor,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(name = "Impacto del cliente", showBackground = true, widthDp = 360)
@Preview(name = "Impacto con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun BloqueImpactoPreview() {
    RescatAppTheme {
        BloqueImpacto(Impacto(rescates = 4, ahorrado = 102.0, kgAprovechados = 3.6))
    }
}
