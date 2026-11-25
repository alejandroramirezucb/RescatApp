package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.FiltrosOfertas
import com.rescatapp.features.explorar.domain.TipoBusquedaExplorar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltrosExplorar(
    filtros: FiltrosOfertas,
    onCategoriaSeleccionada: (Categoria?) -> Unit,
    onOrdenSeleccionado: (OrdenOfertas) -> Unit,
    onTipoSeleccionado: (TipoBusquedaExplorar) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TipoBusquedaExplorar.entries.forEach { tipo ->
                FilterChip(
                    label = { Text(tipo.etiqueta, style = MaterialTheme.typography.labelMedium) },
                    selected = filtros.tipo == tipo && filtros.comercioSeleccionado == null,
                    onClick = { onTipoSeleccionado(tipo) },
                    shape = MaterialTheme.shapes.small,
                    colors = coloresChip()
                )
            }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MenuFiltroExplorar(
                etiqueta = filtros.categoria?.etiqueta ?: "Categorías",
                destacado = filtros.categoria != null,
                icono = Icons.Outlined.Tune,
                indiceActual = filtros.categoria?.ordinal?.plus(1) ?: 0,
                opciones = listOf("Todas" to { onCategoriaSeleccionada(null) }) +
                    Categoria.entries.map { categoria ->
                        categoria.etiqueta to { onCategoriaSeleccionada(categoria) }
                    }
            )
            MenuFiltroExplorar(
                etiqueta = filtros.orden.etiqueta,
                destacado = filtros.orden != OrdenOfertas.RECOMENDADAS,
                indiceActual = filtros.orden.ordinal,
                opciones = OrdenOfertas.entries.map { orden ->
                    orden.etiqueta to { onOrdenSeleccionado(orden) }
                }
            )
        }
    }
}

@Composable
private fun MenuFiltroExplorar(
    etiqueta: String,
    destacado: Boolean,
    indiceActual: Int,
    opciones: List<Pair<String, () -> Unit>>,
    icono: ImageVector? = null
) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        FilterChip(
            label = { Text(etiqueta, style = MaterialTheme.typography.labelMedium) },
            selected = destacado,
            onClick = { abierto = true },
            leadingIcon = icono?.let { imagen ->
                { Icon(imagen, contentDescription = null, modifier = Modifier.size(18.dp)) }
            },
            trailingIcon = {
                Icon(Icons.Outlined.ArrowDropDown, contentDescription = null)
            },
            shape = MaterialTheme.shapes.small,
            colors = coloresChip()
        )
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEachIndexed { indice, (texto, seleccionar) ->
                DropdownMenuItem(
                    text = { Text(texto) },
                    onClick = {
                        seleccionar()
                        abierto = false
                    },
                    trailingIcon = {
                        if (indice == indiceActual) {
                            Icon(Icons.Outlined.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun coloresChip() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
)

@Preview(name = "Filtros compactos", showBackground = true, widthDp = 360)
@Preview(name = "Filtros con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun FiltrosExplorarPreview() {
    RescatAppTheme {
        FiltrosExplorar(
            filtros = FiltrosOfertas(categoria = Categoria.PANADERIA),
            onCategoriaSeleccionada = {},
            onOrdenSeleccionado = {}
        )
    }
}
