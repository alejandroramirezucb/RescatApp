package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BotonSecundario
import com.rescatapp.features.explorar.domain.TipoBusquedaExplorar

@Composable
internal fun TextoResultados(cantidad: Int, singular: String, plural: String) {
    Text(
        text = if (cantidad == 1) "1 $singular" else "$cantidad $plural",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
internal fun EncabezadoComercioSeleccionado(
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
internal fun EstadoVacioExplorar(
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
