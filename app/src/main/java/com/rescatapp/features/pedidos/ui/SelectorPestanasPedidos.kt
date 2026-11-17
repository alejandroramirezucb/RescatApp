package com.rescatapp.features.pedidos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme

@Composable
internal fun SelectorPestanasPedidos(
    pestanaActual: PestanaPedidos,
    onPestanaSeleccionada: (PestanaPedidos) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(
            modifier = Modifier.padding(4.dp).selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PestanaPedidos.entries.forEach { pestana ->
                val seleccionada = pestanaActual == pestana
                Surface(
                    modifier = Modifier.weight(1f).selectable(
                        selected = seleccionada,
                        role = Role.Tab,
                        onClick = { onPestanaSeleccionada(pestana) }
                    ),
                    color = if (seleccionada) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.surfaceContainer
                    },
                    shape = MaterialTheme.shapes.small,
                    shadowElevation = if (seleccionada) 2.dp else 0.dp
                ) {
                    Column(
                        modifier = Modifier.heightIn(min = 48.dp).padding(12.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = pestana.titulo,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (seleccionada) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Pestañas de pedidos", showBackground = true, widthDp = 360)
@Preview(name = "Pestañas con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun SelectorPestanasPedidosPreview() {
    RescatAppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            SelectorPestanasPedidos(PestanaPedidos.ACTIVOS, {})
            SelectorPestanasPedidos(PestanaPedidos.HISTORIAL, {})
        }
    }
}
