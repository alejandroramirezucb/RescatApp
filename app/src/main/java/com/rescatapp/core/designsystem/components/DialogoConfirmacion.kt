package com.rescatapp.core.designsystem.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun DialogoConfirmacion(
    titulo: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onDescartar: () -> Unit,
    textoDescartar: String = "Volver",
    colorConfirmar: Color = MaterialTheme.colorScheme.primary,
    contenido: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDescartar,
        title = { Text(text = titulo, style = MaterialTheme.typography.titleLarge) },
        text = contenido,
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(containerColor = colorConfirmar)
            ) {
                Text(textoConfirmar)
            }
        },
        dismissButton = {
            TextButton(onClick = onDescartar) {
                Text(textoDescartar)
            }
        }
    )
}
