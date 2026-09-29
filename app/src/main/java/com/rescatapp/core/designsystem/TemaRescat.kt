package com.rescatapp.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NaranjaRescat = Color(0xFFD65300)
val VerdeRescat = Color(0xFF34784A)
val FondoRescat = Color(0xFFF4F5F3)

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
