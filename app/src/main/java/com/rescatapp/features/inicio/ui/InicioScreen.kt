package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.theme.Crema
import com.rescatapp.core.model.Categoria

@Composable
fun InicioScreen(
    onExplorar: (Categoria?) -> Unit,
    onVerDetalle: (Int) -> Unit,
    viewModel: InicioViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val verTodas = { onExplorar(null) }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item { EncabezadoInicio(estado.usuario, onBuscar = verTodas) }
        item { BloqueImpacto(estado.impacto) }
        item { CategoriasInicio(onExplorar) }
        item {
            SeccionOfertas(
                titulo = "Ofertas cerca de ti",
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
                colorFondo = Crema,
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
                subtitulo = "Porque te gustan los postres",
                ofertas = estado.ofertas.postres,
                onVerDetalle = onVerDetalle,
                onVerTodo = verTodas,
                textoAccion = "Ver más"
            )
        }
    }
}
