package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.icono
import com.rescatapp.core.designsystem.components.iconoTodasLasCategorias
import com.rescatapp.core.model.Categoria

@Composable
internal fun CategoriasInicio(onExplorar: (Categoria?) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Explora por categoría",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(rellenoLateral)
        )
        LazyRow(
            contentPadding = rellenoLateral,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TarjetaCategoria(
                    etiqueta = "Todos",
                    seleccionada = true,
                    icono = iconoTodasLasCategorias,
                    onClick = { onExplorar(null) }
                )
            }
            items(Categoria.entries) { categoria ->
                TarjetaCategoria(
                    etiqueta = categoria.etiqueta,
                    seleccionada = false,
                    icono = categoria.icono,
                    onClick = { onExplorar(categoria) }
                )
            }
        }
    }
}

@Composable
private fun TarjetaCategoria(
    etiqueta: String,
    seleccionada: Boolean,
    icono: ImageVector,
    onClick: () -> Unit
) {
    val esquema = MaterialTheme.colorScheme
    val colorFondo = if (seleccionada) esquema.primary else esquema.surface
    val colorContenido = if (seleccionada) esquema.onPrimary else esquema.onSurface
    val colorBorde = if (seleccionada) Color.Transparent else esquema.outlineVariant

    Surface(
        onClick = onClick,
        color = colorFondo,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, colorBorde),
        modifier = Modifier.widthIn(min = 96.dp, max = 160.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = colorContenido,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelLarge,
                color = colorContenido,
                textAlign = TextAlign.Center
            )
        }
    }
}
