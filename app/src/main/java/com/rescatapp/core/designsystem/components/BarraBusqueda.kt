package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private const val TEXTO_DE_BUSQUEDA = "Busca comida, restaurantes o productos..."
private val formaBusqueda = RoundedCornerShape(50)

@Composable
fun BarraBusqueda(texto: String, onTextoCambiado: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoCambiado,
        placeholder = { TextoDeBusqueda() },
        singleLine = true,
        shape = formaBusqueda,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun AccesoBusqueda(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        shape = formaBusqueda,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        TextoDeBusqueda(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp))
    }
}

@Composable
private fun TextoDeBusqueda(modifier: Modifier = Modifier) {
    Text(
        text = TEXTO_DE_BUSQUEDA,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Preview(name = "Barra de Búsqueda Editable")
@Composable
private fun BarraBusquedaEditablePreview() {
    BarraBusqueda(texto = "", onTextoCambiado = {})
}

@Preview(name = "Barra de Búsqueda Solo Selección")
@Composable
private fun BarraBusquedaSoloSeleccionPreview() {
    AccesoBusqueda(onClick = {})
}
