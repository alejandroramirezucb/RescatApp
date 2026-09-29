package com.rescatapp.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val coloresRescatApp = lightColorScheme(
    primary = Naranja,
    onPrimary = Color.White,
    primaryContainer = NaranjaSuave,
    onPrimaryContainer = Naranja,
    secondary = Verde,
    onSecondary = Color.White,
    secondaryContainer = VerdeSuave,
    onSecondaryContainer = Verde,
    background = FondoPantalla,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    onSurfaceVariant = TextoSecundario,
    outline = Borde,
    outlineVariant = Borde,
    error = Rojo
)

@Composable
fun RescatAppTheme(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = coloresRescatApp,
        typography = TipografiaRescatApp,
        content = contenido
    )
}
