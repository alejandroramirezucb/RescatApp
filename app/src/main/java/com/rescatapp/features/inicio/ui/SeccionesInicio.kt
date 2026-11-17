package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.EncabezadoSeccion
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.TarjetaOferta
import com.rescatapp.core.model.Oferta

internal val margenLateral = 20.dp
internal val rellenoLateral = PaddingValues(horizontal = margenLateral)

@Composable
internal fun SeccionOfertas(
    titulo: String,
    ofertas: List<Oferta>,
    onVerDetalle: (Int) -> Unit,
    subtitulo: String? = null,
    onVerTodo: (() -> Unit)? = null,
    textoAccion: String = "Ver todo",
    colorFondo: Color = Color.Transparent,
    mostrarHoraLimite: Boolean = false,
    icono: ImageVector? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(colorFondo).padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EncabezadoSeccion(
            titulo = titulo,
            subtitulo = subtitulo,
            modifier = Modifier.padding(rellenoLateral),
            textoAccion = onVerTodo?.let { textoAccion },
            onAccion = { onVerTodo?.invoke() },
            icono = icono
        )
        if (ofertas.isEmpty()) {
            EstadoVacio("No hay ofertas en esta sección por ahora")
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val anchoTarjeta = if (LocalDensity.current.fontScale > 1.2f) {
                    (maxWidth * 0.75f).coerceIn(220.dp, 300.dp)
                } else {
                    (maxWidth * 0.46f).coerceIn(164.dp, 220.dp)
                }
                LazyRow(
                    contentPadding = rellenoLateral,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(ofertas, key = { it.id }) { oferta ->
                        TarjetaOferta(
                            oferta = oferta,
                            onClick = { onVerDetalle(oferta.id) },
                            modifier = Modifier.width(anchoTarjeta),
                            mostrarHoraLimite = mostrarHoraLimite
                        )
                    }
                }
            }
        }
    }
}
