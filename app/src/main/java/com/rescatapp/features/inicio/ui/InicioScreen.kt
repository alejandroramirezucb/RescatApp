package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.AccesoBusqueda
import com.rescatapp.core.designsystem.theme.Crema
import com.rescatapp.core.model.Categoria

@Composable
fun InicioScreen(
    onExplorar: (Categoria?) -> Unit,
    onVerDetalle: (Int) -> Unit,
    viewModel: InicioViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { EncabezadoInicio(estado.usuario, onBuscar = { onExplorar(null) }) }
        item { BloqueImpacto(estado.impacto) }
        item { CategoriasInicio(onExplorar) }
        item {
            SeccionOfertas(
                titulo = "Ofertas cerca de ti",
                subtitulo = "Lo que está disponible ahora",
                ofertas = estado.ofertas.disponibles,
                onVerDetalle = onVerDetalle,
                onVerTodo = { onExplorar(null) }
            )
        }
        item {
            SeccionOfertas(
                titulo = "Se están agotando",
                subtitulo = "No dejes que se acaben",
                ofertas = estado.ofertas.porAgotarse,
                onVerDetalle = onVerDetalle,
                colorFondo = Crema,
                mostrarHoraLimite = true
            )
        }
        item {
            SeccionOfertas(
                titulo = "Lo mejor de hoy",
                subtitulo = "",
                ofertas = estado.ofertas.disponibles,
                onVerDetalle = onVerDetalle,
                onVerTodo = { onExplorar(null) },
                textoAccionOverride = "Ver más"
            )
        }
        item {
            SeccionOfertas(
                titulo = "Puede interesarte",
                subtitulo = "Porque te gustan los postres",
                ofertas = estado.ofertas.porAgotarse,
                onVerDetalle = onVerDetalle,
                onVerTodo = { onExplorar(null) },
                textoAccionOverride = "Ver más"
            )
        }
    }
}

@Composable
private fun EncabezadoInicio(usuario: String, onBuscar: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = margenLateral),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Santa Cruz de la Sierra",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    androidx.compose.material3.Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "¡Hola, $usuario!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                )
                Text(
                    text = "¿Qué vamos a aprovechar hoy?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.TopEnd
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .size(8.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
        AccesoBusqueda(onClick = onBuscar)
    }
}
