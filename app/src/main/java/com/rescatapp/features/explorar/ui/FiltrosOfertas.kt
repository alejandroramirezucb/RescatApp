package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.ExplorarUiState

@Composable
fun FiltrosOfertas(
    estado: ExplorarUiState,
    onBuscar: (String) -> Unit,
    onCategoria: (Categoria?) -> Unit,
    onOrden: (OrdenOfertas) -> Unit
) {
    Column(Modifier.fillMaxWidth().background(Color.White).padding(16.dp)) {
        Text(
            "Explorar",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = estado.texto,
            onValueChange = onBuscar,
            label = { Text("Buscar ofertas") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SelectorCategoria(estado.categoria, onCategoria)
            OrdenOfertas.entries.forEach { orden ->
                FilterChip(
                    selected = estado.orden == orden,
                    onClick = { onOrden(orden) },
                    label = { Text(orden.etiqueta) }
                )
            }
        }
    }
}

@Composable
private fun SelectorCategoria(categoria: Categoria?, onCategoria: (Categoria?) -> Unit) {
    var mostrarCategorias by remember { mutableStateOf(false) }

    Box {
        FilterChip(
            selected = categoria != null,
            onClick = { mostrarCategorias = true },
            label = { Text(categoria?.etiqueta ?: "Filtros") }
        )
        DropdownMenu(
            expanded = mostrarCategorias,
            onDismissRequest = { mostrarCategorias = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todas") },
                onClick = {
                    onCategoria(null)
                    mostrarCategorias = false
                }
            )
            Categoria.entries.forEach { categoriaActual ->
                DropdownMenuItem(
                    text = { Text(categoriaActual.etiqueta) },
                    onClick = {
                        onCategoria(categoriaActual)
                        mostrarCategorias = false
                    }
                )
            }
        }
    }
}
