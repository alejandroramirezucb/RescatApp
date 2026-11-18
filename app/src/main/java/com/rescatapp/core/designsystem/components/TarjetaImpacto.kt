package com.rescatapp.core.designsystem.components

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
fun TarjetaImpacto(titulo: String, impacto: Impacto, modifier: Modifier = Modifier) {
    val indicadores = listOf(
        impacto.rescates.toString() to "Rescates",
        impacto.ahorrado.formatearDinero() to "Ahorrado",
        impacto.kgAprovechados.formatearPeso() to "Aprovechado"
    )
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        shape = MaterialTheme.shapes.large,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth < 280.dp || LocalDensity.current.fontScale > 1.2f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        indicadores.forEach { (valor, etiqueta) ->
                            IndicadorImpacto(valor, etiqueta)
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        indicadores.forEach { (valor, etiqueta) ->
                            IndicadorImpacto(valor, etiqueta, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadorImpacto(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = valor, style = MaterialTheme.typography.titleLarge)
        Text(text = etiqueta, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(name = "Impacto", showBackground = true, widthDp = 360)
@Preview(name = "Impacto con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun TarjetaImpactoPreview() {
    RescatAppTheme {
        TarjetaImpacto(
            titulo = "Tu impacto",
            impacto = Impacto(rescates = 4, ahorrado = 102.0, kgAprovechados = 3.6)
        )
    }
}
