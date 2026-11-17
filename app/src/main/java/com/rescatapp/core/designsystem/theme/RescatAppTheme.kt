package com.rescatapp.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val coloresRescatApp = lightColorScheme(
    primary = Naranja,
    onPrimary = Color.White,
    primaryContainer = NaranjaSuave,
    onPrimaryContainer = SobreNaranjaSuave,
    secondary = Verde,
    onSecondary = Color.White,
    secondaryContainer = VerdeSuave,
    onSecondaryContainer = SobreVerdeSuave,
    tertiary = Verde,
    onTertiary = Color.White,
    tertiaryContainer = VerdeSuave,
    onTertiaryContainer = SobreVerdeSuave,
    background = FondoPantalla,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieSuave,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Superficie,
    surfaceContainerLow = FondoPantalla,
    surfaceContainer = SuperficieSuave,
    surfaceContainerHigh = SuperficieElevada,
    surfaceContainerHighest = Borde,
    surfaceTint = Naranja,
    inverseSurface = TextoPrincipal,
    inverseOnSurface = FondoPantalla,
    inversePrimary = NaranjaSuave,
    outline = Contorno,
    outlineVariant = Borde,
    error = Rojo,
    onError = Color.White,
    errorContainer = RojoSuave,
    onErrorContainer = SobreRojoSuave
)

@Composable
fun RescatAppTheme(contenido: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = coloresRescatApp,
        typography = TipografiaRescatApp,
        shapes = FormasRescatApp,
        content = contenido
    )
}
