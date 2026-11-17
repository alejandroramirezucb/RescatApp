package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.model.Categoria

@Composable
fun InicioScreen(
    onExplorar: (Categoria?) -> Unit,
    onVerDetalle: (Int) -> Unit,
    onVerNotificaciones: () -> Unit = {},
    viewModel: InicioViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val verTodas = { onExplorar(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(top = 20.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            EncabezadoInicio(
                usuario = estado.usuario,
                onBuscar = verTodas,
                conteoNoLeidas = estado.notificacionesNoLeidas,
                onVerNotificaciones = onVerNotificaciones
            )
        }
        item { BloqueImpacto(estado.impacto) }
        item { CategoriasInicio(onExplorar) }
        item {
            SeccionOfertas(
                titulo = "Ofertas disponibles",
                subtitulo = "Lo que está disponible ahora",
                ofertas = estado.ofertas.disponibles,
                onVerDetalle = onVerDetalle,
                onVerTodo = verTodas
            )
        }
        item {
            SeccionOfertas(
                titulo = "Se están agotando",
                subtitulo = "No dejes que se acaben",
                ofertas = estado.ofertas.porAgotarse,
                onVerDetalle = onVerDetalle,
                colorFondo = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                mostrarHoraLimite = true
            )
        }
        item {
            SeccionOfertas(
                titulo = "Lo mejor de hoy",
                ofertas = estado.ofertas.mejoresDelDia,
                onVerDetalle = onVerDetalle,
                onVerTodo = verTodas,
                textoAccion = "Ver más"
            )
        }
        item {
            SeccionOfertas(
                titulo = "Puede interesarte",
                subtitulo = "Descubre los packs de postres disponibles",
                ofertas = estado.ofertas.postres,
                onVerDetalle = onVerDetalle,
                onVerTodo = verTodas,
                textoAccion = "Ver más"
            )
        }
    }
}
