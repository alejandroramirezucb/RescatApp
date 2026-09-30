package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Borde
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.NaranjaVivo
import com.rescatapp.core.designsystem.theme.TextoSecundario
import com.rescatapp.core.model.PasoProgreso

@Composable
fun BarraProgresoPedido(pasos: List<PasoProgreso>, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth()) {
        pasos.forEachIndexed { indice, paso ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LineaProgreso(visible = indice > 0, completada = paso.completado)
                    CirculoPaso(paso)
                    LineaProgreso(
                        visible = indice < pasos.lastIndex,
                        completada = pasos.getOrNull(indice + 1)?.completado == true
                    )
                }
                Text(
                    text = paso.etiqueta,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = if (paso.completado || paso.actual) Naranja else TextoSecundario
                )
            }
        }
    }
}

@Composable
private fun RowScope.LineaProgreso(visible: Boolean, completada: Boolean) {
    val color = when {
        !visible -> Color.Transparent
        completada -> NaranjaVivo
        else -> Borde
    }
    Box(modifier = Modifier.weight(1f).height(2.dp).background(color))
}

@Composable
private fun CirculoPaso(paso: PasoProgreso) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(if (paso.completado) Naranja else Borde, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        when {
            paso.actual -> Box(Modifier.size(8.dp).background(Color.White, CircleShape))

            paso.completado -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
