package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja

@Composable
fun CampoConError(
    etiqueta: String,
    valor: String,
    onValorCambiado: (String) -> Unit,
    ejemplo: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    destacarBordeNaranja: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            placeholder = {
                Text(
                    text = ejemplo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.15f
                ),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.15f
                ),
                focusedBorderColor = if (destacarBordeNaranja) {
                    Naranja
                } else {
                    MaterialTheme.colorScheme.primary
                },
                unfocusedBorderColor = if (destacarBordeNaranja) {
                    Naranja
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Preview(name = "CampoConError Normal", showBackground = true)
@Composable
private fun CampoConErrorNormalPreview() {
    CampoConError(
        etiqueta = "Nombre de la oferta",
        valor = "Pack Salteñas",
        onValorCambiado = {},
        error = null,
        ejemplo = "Ej. Pack Salteñas"
    )
}

@Preview(name = "CampoConError con Error", showBackground = true)
@Composable
private fun CampoConErrorConErrorPreview() {
    CampoConError(
        etiqueta = "Precio normal",
        valor = "",
        onValorCambiado = {},
        error = "Ingresa un precio válido",
        ejemplo = "40",
        tipoTeclado = KeyboardType.Decimal
    )
}
