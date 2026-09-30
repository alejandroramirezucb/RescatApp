package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.rescatapp.core.designsystem.components.ChipSeleccionable
import com.rescatapp.core.designsystem.components.EncabezadoSeccion
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.core.designsystem.components.FilaImpacto
import com.rescatapp.core.designsystem.components.TarjetaOferta
import com.rescatapp.core.designsystem.components.icono
import com.rescatapp.core.designsystem.components.iconoTodasLasCategorias
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.designsystem.theme.VerdeSuave
import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta

internal val margenLateral = 20.dp
private val espacioEntreTarjetas = Arrangement.spacedBy(12.dp)
private val rellenoLateral = PaddingValues(horizontal = margenLateral)

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
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = Verde
            )
            FilaImpacto(impacto = impacto, colorValores = Verde)
        }
    }
}

@Composable
internal fun CategoriasInicio(onExplorar: (Categoria?) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Explora por categoría",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            modifier = Modifier.padding(rellenoLateral)
        )
        LazyRow(
            contentPadding = rellenoLateral,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TarjetaCategoria(
                    etiqueta = "Todos",
                    seleccionado = true,
                    onClick = { onExplorar(null) },
                    icono = iconoTodasLasCategorias
                )
            }
            items(Categoria.entries) { categoria ->
                TarjetaCategoria(
                    etiqueta = categoria.etiqueta,
                    seleccionado = false,
                    onClick = { onExplorar(categoria) },
                    icono = categoria.icono
                )
            }
        }
    }
}

@Composable
private fun TarjetaCategoria(
    etiqueta: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    icono: androidx.compose.ui.graphics.vector.ImageVector?
) {
    val backgroundColor = if (seleccionado) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (seleccionado) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val borderColor = if (seleccionado) {
        Color.Transparent
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Surface(
        onClick = onClick,
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier.width(80.dp).height(80.dp)
    ) {
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp)
        ) {
            if (icono != null) {
                androidx.compose.material3.Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp).padding(bottom = 4.dp)
                )
            }
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun SeccionOfertas(
    titulo: String,
    subtitulo: String,
    ofertas: List<Oferta>,
    onVerDetalle: (Int) -> Unit,
    onVerTodo: (() -> Unit)? = null,
    colorFondo: Color = Color.Transparent,
    mostrarHoraLimite: Boolean = false,
    textoAccionOverride: String = "Ver todo"
) {
    Column(
        modifier = Modifier.fillMaxWidth().background(colorFondo).padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EncabezadoSeccion(
            titulo = titulo,
            subtitulo = subtitulo,
            modifier = Modifier.padding(rellenoLateral),
            textoAccion = onVerTodo?.let { textoAccionOverride },
            onAccion = { onVerTodo?.invoke() }
        )
        if (ofertas.isEmpty()) {
            EstadoVacio("No hay ofertas en esta sección por ahora")
        } else {
            LazyRow(contentPadding = rellenoLateral, horizontalArrangement = espacioEntreTarjetas) {
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
