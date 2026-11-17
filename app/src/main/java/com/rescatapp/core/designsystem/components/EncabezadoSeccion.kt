package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun EncabezadoSeccion(
    titulo: String,
    modifier: Modifier = Modifier,
    subtitulo: String? = null,
    textoAccion: String? = null,
    onAccion: () -> Unit = {},
    icono: ImageVector? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icono?.let {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp).size(18.dp)
                )
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            subtitulo?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        textoAccion?.let {
            TextButton(onClick = onAccion, contentPadding = PaddingValues(0.dp)) {
                Text(
                    text = it,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(name = "Encabezado Sección con Acción Ver Todo", showBackground = true)
@Composable
private fun EncabezadoSeccionPreview() {
    EncabezadoSeccion(
        titulo = "Ofertas cerca de ti",
        subtitulo = "Aprovecha los descuentos del día",
        textoAccion = "Ver todo",
        onAccion = {}
    )
}
