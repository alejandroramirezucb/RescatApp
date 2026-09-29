package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
    }
}

@Composable
private fun EncabezadoInicio(usuario: String, onBuscar: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = margenLateral),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Santa Cruz de la Sierra",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Hola, $usuario",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "¿Qué vamos a aprovechar hoy?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(12.dp))
        AccesoBusqueda(onClick = onBuscar)
    }
}
