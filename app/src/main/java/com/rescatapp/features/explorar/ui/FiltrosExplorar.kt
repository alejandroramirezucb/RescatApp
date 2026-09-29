package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.ChipSeleccionable
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.FiltrosOfertas

@Composable
fun FiltrosExplorar(
    filtros: FiltrosOfertas,
    onCategoriaSeleccionada: (Categoria?) -> Unit,
    onOrdenSeleccionado: (OrdenOfertas) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SelectorCategoria(filtros.categoria, onCategoriaSeleccionada)
        OrdenOfertas.entries.forEach { orden ->
            ChipSeleccionable(
                etiqueta = orden.etiqueta,
                seleccionado = filtros.orden == orden,
                onClick = { onOrdenSeleccionado(orden) }
            )
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
        ChipSeleccionable(
            etiqueta = categoriaActual?.etiqueta ?: "Filtros",
            seleccionado = categoriaActual != null,
            onClick = { menuAbierto = true },
            icono = Icons.Outlined.Tune
        )
        DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
            DropdownMenuItem(text = { Text("Todas") }, onClick = { seleccionar(null) })
            Categoria.entries.forEach { categoria ->
                DropdownMenuItem(
                    text = { Text(categoria.etiqueta) },
                    onClick = { seleccionar(categoria) }
                )
            }
        }
    }
}
