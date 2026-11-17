package com.rescatapp.features.perfil.ui

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
import androidx.compose.ui.unit.dp
import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearPeso

@Composable
internal fun IndicadoresPerfil(impacto: Impacto) {
    val indicadores = listOf(
        impacto.rescates.toString() to "Rescates",
        impacto.ahorrado.formatearDinero() to "Ahorrado",
        impacto.kgAprovechados.formatearPeso() to "Aprovechado"
    )
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Tu impacto", style = MaterialTheme.typography.titleMedium)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                if (maxWidth < 280.dp || LocalDensity.current.fontScale > 1.2f) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        indicadores.forEach { (valor, etiqueta) ->
                            IndicadorPerfil(valor, etiqueta)
                        }
                    }
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        indicadores.forEach { (valor, etiqueta) ->
                            IndicadorPerfil(valor, etiqueta, Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadorPerfil(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = valor, style = MaterialTheme.typography.titleLarge)
        Text(text = etiqueta, style = MaterialTheme.typography.bodySmall)
    }
}
