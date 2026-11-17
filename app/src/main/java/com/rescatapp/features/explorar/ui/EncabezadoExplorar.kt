package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.FiltrosOfertas

@Composable
internal fun EncabezadoExplorar(
    filtros: FiltrosOfertas,
    hayFiltros: Boolean,
    onBuscar: (String) -> Unit,
    onCategoriaSeleccionada: (Categoria?) -> Unit,
    onOrdenSeleccionado: (OrdenOfertas) -> Unit,
    onLimpiarFiltros: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "Explorar", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Encuentra tu próximo pack y dale una nueva oportunidad a la comida.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BusquedaExplorar(texto = filtros.texto, onTextoCambiado = onBuscar)
                FiltrosExplorar(filtros, onCategoriaSeleccionada, onOrdenSeleccionado)
                if (hayFiltros) {
                    TextButton(onClick = onLimpiarFiltros) {
                        Text("Limpiar filtros")
                    }
                }
            }
        }
    }
}

@Preview(name = "Explorar sin resultados", showBackground = true, widthDp = 360)
@Preview(name = "Explorar con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun ExplorarContenidoPreview() {
    RescatAppTheme {
        ExplorarContenido(
            estado = ExplorarUiState(filtros = FiltrosOfertas(texto = "Pan artesanal")),
            onBuscar = {},
            onCategoriaSeleccionada = {},
            onOrdenSeleccionado = {},
            onLimpiarFiltros = {},
            onVerDetalle = {}
        )
    }
}
