package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.AvisoTemporal
import com.rescatapp.core.designsystem.components.DialogoConfirmacion
import com.rescatapp.core.model.Oferta

@Composable
fun OfertasNegocioScreen(viewModel: OfertasNegocioViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    OfertasNegocioContenido(
        estado = estado,
        onSeleccion = viewModel::seleccionarPestana,
        onEditar = viewModel::actualizarOferta,
        onEliminar = viewModel::eliminarOferta,
        onCambiarEstado = viewModel::cambiarEstado,
        onLimpiarError = viewModel::limpiarError
    )
}

@Composable
internal fun OfertasNegocioContenido(
    estado: OfertasNegocioUiState,
    onSeleccion: (PestanaOfertas) -> Unit,
    onEditar: (Oferta, () -> Unit) -> Unit,
    onEliminar: (Oferta, () -> Unit) -> Unit,
    onCambiarEstado: (Oferta) -> Unit,
    onLimpiarError: () -> Unit
) {
    var ofertaParaEditar by remember { mutableStateOf<Oferta?>(null) }
    var ofertaParaEliminar by remember { mutableStateOf<Oferta?>(null) }

    ofertaParaEditar?.let { oferta ->
        DialogoEditarOferta(
            oferta = oferta,
            onGuardar = { actualizada -> onEditar(actualizada) { ofertaParaEditar = null } },
            onDescartar = { ofertaParaEditar = null }
        )
    }

    ofertaParaEliminar?.let { oferta ->
        DialogoConfirmacion(
            titulo = "Eliminar oferta",
            textoConfirmar = "Eliminar",
            textoDescartar = "Cancelar",
            colorConfirmar = MaterialTheme.colorScheme.error,
            icono = Icons.Outlined.Delete,
            onConfirmar = { onEliminar(oferta) { ofertaParaEliminar = null } },
            onDescartar = { ofertaParaEliminar = null }
        ) {
            PreguntaEliminarOferta(oferta)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ListaOfertasNegocio(
            estado = estado,
            onSeleccion = onSeleccion,
            onEditar = { ofertaParaEditar = it },
            onEliminar = { ofertaParaEliminar = it },
            onCambiarEstado = onCambiarEstado
        )
        AvisoTemporal(
            estado.error,
            onLimpiarError,
            Modifier.align(Alignment.BottomCenter).padding(bottom = 72.dp)
        )
    }
}

@Composable
private fun PreguntaEliminarOferta(oferta: Oferta) {
    Text(
        text = buildAnnotatedString {
            append("¿Eliminar «")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(oferta.nombre) }
            append("» de tus ofertas?")
        },
        style = MaterialTheme.typography.bodyLarge
    )
}
