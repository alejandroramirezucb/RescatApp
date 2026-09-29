package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BarraBusqueda
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.TarjetaOferta
import com.rescatapp.core.model.Oferta

@Composable
fun ExplorarScreen(onVerDetalle: (Int) -> Unit, viewModel: ExplorarViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(text = "Explorar", style = MaterialTheme.typography.headlineSmall)
                BarraBusqueda(texto = estado.filtros.texto, onTextoCambiado = viewModel::buscar)
                FiltrosExplorar(
                    filtros = estado.filtros,
                    onCategoriaSeleccionada = viewModel::seleccionarCategoria,
                    onOrdenSeleccionado = viewModel::seleccionarOrden
                )
            }
        }
        Text(
            text = "${estado.cantidad} ofertas encontradas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        if (estado.ofertas.isEmpty()) {
            EstadoVacio("No encontramos ofertas con esos filtros")
        } else {
            CuadriculaOfertas(estado.ofertas, onVerDetalle)
        }
    }
}

@Composable
private fun CuadriculaOfertas(ofertas: List<Oferta>, onVerDetalle: (Int) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 96.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ofertas, key = { it.id }) { oferta ->
            TarjetaOferta(oferta = oferta, onClick = { onVerDetalle(oferta.id) })
        }
    }
}
