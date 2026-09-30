package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.EncabezadoSeccion
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.FilaImpacto
import com.rescatapp.core.designsystem.components.TarjetaOferta
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.designsystem.theme.VerdeSuave
import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.model.Oferta

internal val margenLateral = 20.dp
internal val rellenoLateral = PaddingValues(horizontal = margenLateral)

@Composable
internal fun BloqueImpacto(impacto: Impacto) {
    Surface(
        color = VerdeSuave,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().padding(rellenoLateral)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tu impacto esta semana",
                style = MaterialTheme.typography.titleMedium,
                color = Verde
            )
            FilaImpacto(impacto = impacto, colorValores = Verde)
        }
    }
}

@Composable
internal fun SeccionOfertas(
    titulo: String,
    ofertas: List<Oferta>,
    onVerDetalle: (Int) -> Unit,
    subtitulo: String? = null,
    onVerTodo: (() -> Unit)? = null,
    textoAccion: String = "Ver todo",
    colorFondo: Color = Color.Transparent,
    mostrarHoraLimite: Boolean = false
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
            onAccion = { onVerTodo?.invoke() }
        )
        if (ofertas.isEmpty()) {
            EstadoVacio("No hay ofertas en esta sección por ahora")
        } else {
            LazyRow(
                contentPadding = rellenoLateral,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ofertas, key = { it.id }) { oferta ->
                    TarjetaOferta(
                        oferta = oferta,
                        onClick = { onVerDetalle(oferta.id) },
                        modifier = Modifier.width(172.dp),
                        mostrarHoraLimite = mostrarHoraLimite
                    )
                }
            }
        }
    }
}
