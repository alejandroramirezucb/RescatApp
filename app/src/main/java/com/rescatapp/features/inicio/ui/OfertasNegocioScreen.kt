package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.DialogoConfirmacion
import com.rescatapp.core.model.Oferta

@Composable
fun OfertasNegocioScreen(viewModel: OfertasNegocioViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    OfertasNegocioContenido(
        estado = estado,
        onSeleccion = viewModel::seleccionarPestana,
        onReintentar = viewModel::cargarOfertasRemotas,
        onPublicar = viewModel::publicarEnLinea,
        onEditarLocal = viewModel::actualizarOferta,
        onEditarRemota = viewModel::editarOfertaRemota,
        onEliminar = viewModel::eliminarOfertaRemota,
        onEliminarLocal = viewModel::eliminarOfertaLocal,
        onReintentarOperacion = viewModel::reintentarOperacion,
        onLimpiarMensaje = viewModel::limpiarMensaje,
        onCambiarEstado = viewModel::cambiarEstado
    )
}

@Composable
internal fun OfertasNegocioContenido(
    estado: OfertasNegocioUiState,
    onSeleccion: (PestanaOfertas) -> Unit,
    onReintentar: () -> Unit,
    onPublicar: (Oferta) -> Unit,
    onEditarLocal: (Oferta, () -> Unit) -> Unit,
    onEditarRemota: (Oferta) -> Unit,
    onEliminar: (Oferta) -> Unit,
    onEliminarLocal: (Oferta, () -> Unit) -> Unit,
    onReintentarOperacion: () -> Unit,
    onLimpiarMensaje: () -> Unit,
    onCambiarEstado: ((Oferta) -> Unit)? = null
) {
    var ofertaParaEditar by remember { mutableStateOf<Pair<Oferta, Boolean>?>(null) }
    var ofertaParaEliminar by remember { mutableStateOf<Pair<Oferta, Boolean>?>(null) }

    ofertaParaEditar?.let { (oferta, esRemota) ->
        DialogoEditarOferta(
            oferta = oferta,
            onGuardar = { actualizada ->
                if (esRemota) {
                    onEditarRemota(actualizada)
                    ofertaParaEditar = null
                } else {
                    onEditarLocal(actualizada) { ofertaParaEditar = null }
                }
            },
            onDescartar = { ofertaParaEditar = null }
        )
    }

    ofertaParaEliminar?.let { (oferta, esRemota) ->
        DialogoConfirmacion(
            titulo = "Eliminar oferta",
            textoConfirmar = "Eliminar",
            textoDescartar = if (esRemota) "Cancelar" else "Volver",
            colorConfirmar = MaterialTheme.colorScheme.error,
            icono = Icons.Outlined.Delete,
            onConfirmar = {
                if (!estado.ocupado) {
                    if (esRemota) {
                        onEliminar(oferta)
                        ofertaParaEliminar = null
                    } else {
                        onEliminarLocal(oferta) { ofertaParaEliminar = null }
                    }
                }
            },
            onDescartar = { ofertaParaEliminar = null }
        ) {
            if (esRemota) {
                PreguntaEliminarOferta(oferta, esRemota = true)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    PreguntaEliminarOferta(oferta, esRemota = false)
                    if (estado.eliminandoLocal) Text("Eliminando oferta.")
                    estado.errorLocal?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }

    ListaOfertasNegocio(
        estado = estado,
        onSeleccion = onSeleccion,
        onReintentar = onReintentar,
        onPublicar = onPublicar,
        onEditar = { ofertaParaEditar = it to (estado.pestana == PestanaOfertas.EN_LINEA) },
        onEliminar = { ofertaParaEliminar = it to (estado.pestana == PestanaOfertas.EN_LINEA) },
        onReintentarOperacion = onReintentarOperacion,
        onLimpiarMensaje = onLimpiarMensaje,
        onCambiarEstado = onCambiarEstado
    )
}

@Composable
private fun PreguntaEliminarOferta(oferta: Oferta, esRemota: Boolean) {
    Text(
        text = buildAnnotatedString {
            append("¿Eliminar «")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(oferta.nombre) }
            append(" de tus ofertas?")
        },
        style = MaterialTheme.typography.bodyLarge
    )
}
