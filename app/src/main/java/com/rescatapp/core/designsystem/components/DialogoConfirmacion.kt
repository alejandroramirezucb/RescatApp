package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.RescatAppTheme

@Composable
fun DialogoConfirmacion(
    titulo: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit,
    textoDescartar: String = "Volver",
    colorConfirmar: Color = MaterialTheme.colorScheme.primary,
    icono: ImageVector = Icons.Outlined.Info,
    contenidoDesplazable: Boolean = true,
    contenido: @Composable () -> Unit
) {
    val desplazamiento = rememberScrollState()
    AlertDialog(
        onDismissRequest = onDescartar,
        shape = MaterialTheme.shapes.extraLarge,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        icon = {
            Surface(
                color = colorConfirmar.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorConfirmar,
                    modifier = Modifier.padding(12.dp).size(24.dp)
                )
            }
        },
        title = { Text(text = titulo, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                modifier = if (contenidoDesplazable) {
                    Modifier.fillMaxWidth().verticalScroll(desplazamiento)
                } else {
                    Modifier.fillMaxWidth()
                },
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                contenido()
            }
        },
        confirmButton = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onConfirmar,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorConfirmar,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(
                        text = textoConfirmar,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center
                    )
                }
                BotonSecundario(texto = textoDescartar, onClick = onDescartar)
            }
        }
    )
}

@Preview(name = "Confirmación con texto grande", widthDp = 320, fontScale = 1.5f)
@Composable
private fun DialogoConfirmacionPreview() {
    RescatAppTheme {
        DialogoConfirmacion(
            titulo = "Eliminar oferta local",
            textoConfirmar = "Eliminar",
            onConfirmar = {},
            onDescartar = {},
            colorConfirmar = MaterialTheme.colorScheme.error
        ) {
            Text("¿Eliminar «Pack Sorpresa» de las ofertas locales?")
        }
    }
}
