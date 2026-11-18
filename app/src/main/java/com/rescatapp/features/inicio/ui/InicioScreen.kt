package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.ofertaEjemplo
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.inicio.domain.SeleccionarOfertasInicioUseCase

@Composable
fun InicioScreen(
    onExplorar: (Categoria?) -> Unit,
    onVerDetalle: (Int) -> Unit,
    onVerNotificaciones: () -> Unit = {},
    viewModel: InicioViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    InicioContenido(estado, onExplorar, onVerDetalle, onVerNotificaciones)
}

@Composable
internal fun InicioContenido(
    estado: InicioUiState,
    onExplorar: (Categoria?) -> Unit,
    onVerDetalle: (Int) -> Unit,
    onVerNotificaciones: () -> Unit
) {
    val verTodas = { onExplorar(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(top = 20.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            EncabezadoInicio(
                usuario = estado.usuario,
                direccion = estado.direccion,
                onBuscar = verTodas,
                conteoNoLeidas = estado.notificacionesNoLeidas,
                onVerNotificaciones = onVerNotificaciones
            )
        }
        item { BannerOportunidades(onExplorar = verTodas) }
        item { CategoriasInicio(onExplorar) }
        item {
            SeccionOfertas(
                titulo = "Ofertas disponibles",
                subtitulo = "Lo que está disponible ahora",
                ofertas = estado.ofertas.disponibles,
                onVerDetalle = onVerDetalle,
                onVerTodo = verTodas,
                icono = Icons.Outlined.LocalFireDepartment
            )
        }
        item {
            SeccionOfertas(
                titulo = "Se están agotando",
                subtitulo = "No dejes que se acaben",
                ofertas = estado.ofertas.porAgotarse,
                onVerDetalle = onVerDetalle,
                colorFondo = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                mostrarHoraLimite = true,
                icono = Icons.Outlined.AccessTime
            )
        }
        item {
            SeccionOfertas(
                titulo = "Lo mejor de hoy",
                ofertas = estado.ofertas.mejoresDelDia,
                onVerDetalle = onVerDetalle,
                onVerTodo = verTodas,
                textoAccion = "Ver más",
                icono = Icons.Outlined.Star
            )
        }
        item { ComerciosInicio(estado.ofertas.disponibles, verTodas) }
        item { BloqueImpacto(estado.impacto) }
        item {
            SeccionOfertas(
                titulo = "Puede interesarte",
                subtitulo = "Descubre los packs de postres disponibles",
                ofertas = estado.ofertas.postres,
                onVerDetalle = onVerDetalle,
                onVerTodo = verTodas,
                textoAccion = "Ver más",
                icono = Icons.Outlined.Visibility
            )
        }
        item { UltimasOfertasInicio(estado.ofertas.porAgotarse, onVerDetalle) }
    }
}

@Preview(name = "Inicio inspirado en Figma", widthDp = 390, heightDp = 1100, showBackground = true)
@Preview(name = "Inicio Figma con texto grande", widthDp = 320, heightDp = 900, fontScale = 1.5f)
@Composable
private fun InicioContenidoPreview() {
    val pack = ofertaEjemplo
    val ofertas = listOf(
        pack,
        pack.copy(id = 2, nombre = "Pack Croissants", cantidadDisponible = 2),
        pack.copy(
            id = 3,
            nombre = "Caja Cupcakes",
            categoria = Categoria.POSTRES,
            cantidadDisponible = 2
        )
    )
    RescatAppTheme {
        InicioContenido(
            estado = InicioUiState(
                usuario = "Krishna",
                impacto = Impacto(3, 45.0, 1.8),
                ofertas = SeleccionarOfertasInicioUseCase()(ofertas)
            ),
            onExplorar = {},
            onVerDetalle = {},
            onVerNotificaciones = {}
        )
    }
}
