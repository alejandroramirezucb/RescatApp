package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.TextoPrincipal
import com.rescatapp.core.util.formatearDescuento

@Composable
fun InsigniaDescuento(porcentajeDescuento: Int, modifier: Modifier = Modifier) {
    EtiquetaSobreImagen(porcentajeDescuento.formatearDescuento(), Naranja, modifier)
}

@Composable
fun EtiquetaHoraLimite(horaLimite: String, modifier: Modifier = Modifier) {
    EtiquetaSobreImagen("Hasta $horaLimite", TextoPrincipal.copy(alpha = 0.85f), modifier)
}

@Composable
private fun EtiquetaSobreImagen(texto: String, colorFondo: Color, modifier: Modifier) {
    Surface(modifier = modifier, color = colorFondo, shape = RoundedCornerShape(6.dp)) {
        Text(
            text = texto,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Preview(name = "Insignia Descuento -44%")
@Composable
private fun InsigniaDescuentoPreview() {
    InsigniaDescuento(porcentajeDescuento = 44)
}

@Preview(name = "Etiqueta Hora Límite")
@Composable
private fun EtiquetaHoraLimitePreview() {
    EtiquetaHoraLimite(horaLimite = "22:00")
}
