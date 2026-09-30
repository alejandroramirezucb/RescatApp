package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CampoConError(
    etiqueta: String,
    valor: String,
    onValorCambiado: (String) -> Unit,
    error: String?,
    ejemplo: String,
    modifier: Modifier = Modifier,
    tipoTeclado: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambiado,
        label = { Text(etiqueta) },
        placeholder = { Text(ejemplo) },
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
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
