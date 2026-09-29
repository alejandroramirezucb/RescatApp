package com.rescatapp.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val colores = lightColorScheme(
    primary = NaranjaRescat,
    secondary = VerdeRescat,
    background = FondoRescat,
    surface = Color.White
)

@Composable
fun TemaRescat(contenido: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colores, content = contenido)
}
