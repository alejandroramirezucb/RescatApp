package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Oferta

@Composable
internal fun ListaOfertasNegocio(
    estado: OfertasNegocioUiState,
    onSeleccion: (PestanaOfertas) -> Unit,
    onEditar: (Oferta) -> Unit,
    onEliminar: (Oferta) -> Unit,
    onCambiarEstado: (Oferta) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { EncabezadoOfertasNegocio() }
        item { SelectorOfertasNegocio(pestana = estado.pestana, onSeleccion = onSeleccion) }
        if (estado.ocupado) {
            item { ProgresoOfertas("Guardando cambios…") }
        }
        if (estado.ofertasVisibles.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay ofertas ${estado.pestana.titulo.lowercase()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        items(estado.ofertasVisibles, key = { it.id }) { oferta ->
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                TarjetaOfertaNegocio(
                    oferta = oferta,
                    habilitada = !estado.ocupado,
                    onEditar = { onEditar(oferta) },
                    onEliminar = { onEliminar(oferta) },
                    onCambiarEstado = { onCambiarEstado(oferta) }
                )
            }
        }
    }
}
