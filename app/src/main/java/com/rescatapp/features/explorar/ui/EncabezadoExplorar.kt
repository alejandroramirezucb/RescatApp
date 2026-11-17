package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
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
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explorar",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
            if (hayFiltros) {
                TextButton(onClick = onLimpiarFiltros) { Text("Limpiar filtros") }
            }
        }
        BusquedaExplorar(texto = filtros.texto, onTextoCambiado = onBuscar)
        FiltrosExplorar(filtros, onCategoriaSeleccionada, onOrdenSeleccionado)
    }
}

@Preview(name = "Explorar en dos columnas", showBackground = true, widthDp = 390, heightDp = 1000)
@Preview(name = "Explorar en pantalla pequeña", widthDp = 320, heightDp = 1000)
@Preview(name = "Explorar con texto grande", widthDp = 320, heightDp = 1000, fontScale = 1.5f)
@Composable
private fun ExplorarContenidoPreview() {
    val ejemplo = Oferta(
        id = 1,
        nombre = "Pack Sorpresa",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        descripcion = "Panes del día",
        pesoKg = 1.0,
        precioNormal = 45.0,
        precioRescate = 25.0,
        cantidadDisponible = 5,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00"
    )
    RescatAppTheme {
        ExplorarContenido(
            estado = ExplorarUiState(
                ofertas = listOf(
                    ejemplo,
                    ejemplo.copy(id = 2, nombre = "Pack Croissants", cantidadDisponible = 3),
                    ejemplo.copy(id = 3, nombre = "Pizza Familiar", cantidadDisponible = 2),
                    ejemplo.copy(id = 4, nombre = "2 Pizzas Medianas", cantidadDisponible = 1)
                )
            ),
            onBuscar = {},
            onCategoriaSeleccionada = {},
            onOrdenSeleccionado = {},
            onLimpiarFiltros = {},
            onVerDetalle = {}
        )
    }
}
