package com.rescatapp.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

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
