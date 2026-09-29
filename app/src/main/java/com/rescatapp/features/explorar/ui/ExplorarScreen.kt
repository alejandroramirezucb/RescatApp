package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.FondoRescat
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.ExplorarUiState

@Composable
fun ExplorarScreen(
    estado: ExplorarUiState,
    onBuscar: (String) -> Unit,
    onCategoria: (Categoria?) -> Unit,
    onOrden: (OrdenOfertas) -> Unit,
    onVerDetalle: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize().background(FondoRescat)) {
        FiltrosOfertas(
            estado = estado,
            onBuscar = onBuscar,
            onCategoria = onCategoria,
            onOrden = onOrden
        )

        Text(
            text = "${estado.cantidad} ofertas encontradas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 16.dp, top = 14.dp, bottom = 12.dp)
        )

        if (estado.ofertas.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No encontramos ofertas", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.ofertas, key = { it.id }) { oferta ->
                    TarjetaOferta(oferta = oferta, onClick = { onVerDetalle(oferta.id) })
                }
            }
        }
    }
}
