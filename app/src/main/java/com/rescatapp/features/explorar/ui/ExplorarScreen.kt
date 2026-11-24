package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BotonSecundario
import com.rescatapp.core.designsystem.components.TarjetaComercio
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.TipoBusquedaExplorar

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
        onTipoSeleccionado = viewModel::seleccionarTipoBusqueda,
        onComercioSeleccionado = viewModel::seleccionarComercio,
        onLimpiarFiltros = {
            foco.clearFocus()
            viewModel.limpiarFiltros()
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
    onVerDetalle: (Int) -> Unit,
    onTipoSeleccionado: (TipoBusquedaExplorar) -> Unit = {},
    onComercioSeleccionado: (String?) -> Unit = {}
) {
    val hayFiltros = estado.filtros.texto.isNotEmpty() || estado.filtros.categoria != null ||
        estado.filtros.orden != OrdenOfertas.RECOMENDADAS ||
        estado.filtros.tipo != TipoBusquedaExplorar.TODO ||
        estado.filtros.comercioSeleccionado != null
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
                    onTipoSeleccionado = onTipoSeleccionado,
                    onLimpiarComercio = { onComercioSeleccionado(null) },
                    onLimpiarFiltros = onLimpiarFiltros
                )
            }
            estado.filtros.comercioSeleccionado?.let { comercio ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EncabezadoComercioSeleccionado(
                        comercio = comercio,
                        cantidadOfertas = estado.cantidad,
                        onVolverATodos = { onComercioSeleccionado(null) }
                    )
                }
            }
            if (estado.sinResultados) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EstadoVacioExplorar(
                        hayFiltros = hayFiltros,
                        tipo = estado.filtros.tipo,
                        onLimpiarFiltros = onLimpiarFiltros
                    )
                }
            } else {
                if (estado.comerciosVisibles.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = if (estado.cantidadComercios == 1) {
                                "1 negocio encontrado"
                            } else {
                                "${estado.cantidadComercios} negocios encontrados"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (estado.filtros.tipo == TipoBusquedaExplorar.NEGOCIOS) {
                        items(
                            items = estado.comerciosVisibles,
                            key = { "comercio-${it.nombre}" }
                        ) { comercio ->
                            TarjetaComercio(
                                comercio = comercio,
                                onClick = { onComercioSeleccionado(comercio.nombre) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                items(
                                    items = estado.comerciosVisibles,
                                    key = { "comercio-${it.nombre}" }
                                ) { comercio ->
                                    TarjetaComercio(
                                        comercio = comercio,
                                        onClick = { onComercioSeleccionado(comercio.nombre) },
                                        modifier = Modifier.width(168.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                if (estado.ofertasVisibles.isNotEmpty()) {
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
                    items(estado.ofertasVisibles, key = { it.id }) { oferta ->
                        TarjetaOfertaExplorar(
                            oferta = oferta,
                            onClick = { onVerDetalle(oferta.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EncabezadoComercioSeleccionado(
    comercio: String,
    cantidadOfertas: Int,
    onVolverATodos: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = comercio,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (cantidadOfertas == 1) {
                        "1 oferta activa"
                    } else {
                        "$cantidadOfertas ofertas activas"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onVolverATodos) { Text("Ver todos") }
        }
    }
}

@Composable
private fun EstadoVacioExplorar(
    hayFiltros: Boolean,
    tipo: TipoBusquedaExplorar,
    onLimpiarFiltros: () -> Unit
) {
    val titulo = when {
        !hayFiltros -> "Aún no hay ofertas ni negocios para explorar"
        tipo == TipoBusquedaExplorar.NEGOCIOS -> "No encontramos negocios con esos filtros"
        tipo == TipoBusquedaExplorar.PRODUCTOS -> "No encontramos productos con esos filtros"
        else -> "No encontramos productos ni negocios con esos filtros"
    }
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                text = if (hayFiltros) {
                    "Prueba otro nombre, cambia la categoría o limpia los filtros."
                } else {
                    "Los nuevos comercios y packs aparecerán aquí cuando estén disponibles."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (hayFiltros) {
                BotonSecundario(texto = "Ver todo el catálogo", onClick = onLimpiarFiltros)
            }
        }
    }
}
