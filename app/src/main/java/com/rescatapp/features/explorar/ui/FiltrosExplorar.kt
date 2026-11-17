package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.FiltrosOfertas

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltrosExplorar(
    filtros: FiltrosOfertas,
    onCategoriaSeleccionada: (Categoria?) -> Unit,
    onOrdenSeleccionado: (OrdenOfertas) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Categoría", style = MaterialTheme.typography.labelLarge)
        SelectorCategoria(filtros.categoria, onCategoriaSeleccionada)
        Text(text = "Ordenar por", style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OrdenOfertas.entries.forEach { orden ->
                FilterChip(
                    label = { Text(orden.etiqueta) },
                    selected = filtros.orden == orden,
                    onClick = { onOrdenSeleccionado(orden) },
                    leadingIcon = if (filtros.orden == orden) {
                        { Icon(Icons.Outlined.Check, contentDescription = null) }
                    } else {
                        null
                    },
                    shape = MaterialTheme.shapes.small,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun SelectorCategoria(
    categoriaActual: Categoria?,
    onCategoriaSeleccionada: (Categoria?) -> Unit
) {
    var menuAbierto by remember { mutableStateOf(false) }
    val seleccionar = { categoria: Categoria? ->
        onCategoriaSeleccionada(categoria)
        menuAbierto = false
    }
    Box {
        FilterChip(
            label = { Text(categoriaActual?.etiqueta ?: "Todas las categorías") },
            selected = categoriaActual != null,
            onClick = { menuAbierto = true },
            leadingIcon = { Icon(Icons.Outlined.Tune, contentDescription = null) },
            trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, contentDescription = null) },
            shape = MaterialTheme.shapes.small,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )
        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
            DropdownMenuItem(
                text = { Text("Todas") },
                onClick = { seleccionar(null) },
                trailingIcon = {
                    if (categoriaActual ==
                        null
                    ) {
                        Icon(Icons.Outlined.Check, contentDescription = null)
                    }
                }
            )
            Categoria.entries.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria.etiqueta) },
                    onClick = { seleccionar(categoria) },
                    trailingIcon = {
                        if (categoriaActual == categoria) {
                            Icon(Icons.Outlined.Check, contentDescription = null)
                        }
                    }
                )
            }
        }
    }
}

@Preview(name = "Filtros de explorar", showBackground = true, widthDp = 360)
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
