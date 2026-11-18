package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BotonSecundario
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas

@Composable
fun ExplorarScreen(onVerDetalle: (Int) -> Unit, viewModel: ExplorarViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val filtros by viewModel.filtros.collectAsStateWithLifecycle()
    val foco = LocalFocusManager.current
    ExplorarContenido(
        estado = estado.copy(filtros = filtros),
        onBuscar = viewModel::buscar,
        onCategoriaSeleccionada = viewModel::seleccionarCategoria,
        onOrdenSeleccionado = viewModel::seleccionarOrden,
        onLimpiarFiltros = {
            foco.clearFocus()
            viewModel.buscar("")
            viewModel.seleccionarCategoria(null)
            viewModel.seleccionarOrden(OrdenOfertas.RECOMENDADAS)
        },
        onVerDetalle = onVerDetalle
    )
}

@Composable
internal fun ExplorarContenido(
    estado: ExplorarUiState,
    onBuscar: (String) -> Unit,
    onCategoriaSeleccionada: (Categoria?) -> Unit,
    onOrdenSeleccionado: (OrdenOfertas) -> Unit,
    onLimpiarFiltros: () -> Unit,
    onVerDetalle: (Int) -> Unit
) {
    val hayFiltros = estado.filtros.texto.isNotEmpty() || estado.filtros.categoria != null ||
        estado.filtros.orden != OrdenOfertas.RECOMENDADAS
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        val columnas = when {
            LocalDensity.current.fontScale > 1.2f -> GridCells.Adaptive(280.dp)
            maxWidth < 300.dp -> GridCells.Fixed(1)
            maxWidth < 600.dp -> GridCells.Fixed(2)
            else -> GridCells.Adaptive(180.dp)
        }
        LazyVerticalGrid(
            columns = columnas,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 12.dp, top = 16.dp, end = 12.dp, bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                EncabezadoExplorar(
                    filtros = estado.filtros,
                    hayFiltros = hayFiltros,
                    onBuscar = onBuscar,
                    onCategoriaSeleccionada = onCategoriaSeleccionada,
                    onOrdenSeleccionado = onOrdenSeleccionado,
                    onLimpiarFiltros = onLimpiarFiltros
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = if (estado.cantidad == 1) {
                        "1 oferta encontrada"
                    } else {
                        "${estado.cantidad} ofertas encontradas"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (estado.ofertas.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (hayFiltros) {
                                    "No encontramos ofertas con esos filtros"
                                } else {
                                    "Aún no hay ofertas para explorar"
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = if (hayFiltros) {
                                    "Prueba otro nombre, cambia la categoría o limpia los filtros."
                                } else {
                                    "Los nuevos packs aparecerán aquí cuando estén disponibles."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (hayFiltros) {
                                BotonSecundario(
                                    texto = "Ver todas las ofertas",
                                    onClick = onLimpiarFiltros
                                )
                            }
                        }
                    }
                }
            } else {
                items(estado.ofertas, key = { it.id }) { oferta ->
                    TarjetaOfertaExplorar(oferta = oferta, onClick = { onVerDetalle(oferta.id) })
                }
            }
        }
    }
}
