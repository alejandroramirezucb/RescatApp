package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ChipSeleccionable(
    etiqueta: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    icono: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(etiqueta) },
        leadingIcon = icono?.let {
            { Icon(it, contentDescription = null, modifier = Modifier.size(18.dp)) }
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    )
}

@Composable
fun ChipCategoria(
    etiqueta: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    icono: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    ChipSeleccionable(
        etiqueta = etiqueta,
        seleccionado = seleccionado,
        onClick = onClick,
        icono = icono,
        modifier = modifier
    )
}

@Composable
fun ChipOpcion(
    etiqueta: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ChipSeleccionable(
        etiqueta = etiqueta,
        seleccionado = seleccionado,
        onClick = onClick,
        modifier = modifier
    )
}

@Preview(name = "Chip Categoría Seleccionado y No Seleccionado")
@Composable
private fun ChipCategoriaPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ChipCategoria(
            etiqueta = "Panadería",
            seleccionado = true,
            onClick = {},
            icono = Icons.Default.BakeryDining
        )
        ChipCategoria(
            etiqueta = "Panadería",
            seleccionado = false,
            onClick = {},
            icono = Icons.Default.BakeryDining
        )
    }
}

@Preview(name = "Chip Opción Seleccionado y No Seleccionado")
@Composable
private fun ChipOpcionPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ChipOpcion(etiqueta = "Recomendadas", seleccionado = true, onClick = {})
        ChipOpcion(etiqueta = "Mayor descuento", seleccionado = false, onClick = {})
    }
}
