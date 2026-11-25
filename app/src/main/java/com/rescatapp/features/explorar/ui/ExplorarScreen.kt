package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.TarjetaComercio
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.FiltrosOfertas
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
    val hayFiltros = estado.filtros != FiltrosOfertas()
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
            }
            if (estado.comerciosVisibles.isNotEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    TextoResultados(
                        cantidad = estado.cantidadComercios,
                        singular = "negocio encontrado",
                        plural = "negocios encontrados"
                    )
                }
                if (estado.filtros.tipo == TipoBusquedaExplorar.NEGOCIOS) {
                    items(estado.comerciosVisibles, key = { it.nombre }) { comercio ->
                        TarjetaComercio(
                            comercio = comercio,
                            onClick = { onComercioSeleccionado(comercio.nombre) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(estado.comerciosVisibles, key = { it.nombre }) { comercio ->
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
                    TextoResultados(estado.cantidad, "oferta encontrada", "ofertas encontradas")
                }
                items(estado.ofertasVisibles, key = { it.id }) { oferta ->
                    TarjetaOfertaExplorar(oferta = oferta, onClick = { onVerDetalle(oferta.id) })
                }
            }
        }
    }
}
