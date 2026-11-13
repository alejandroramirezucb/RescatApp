package com.rescatapp.features.inicio.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        onReintentarOperacion = viewModel::reintentarOperacion,
        onLimpiarMensaje = viewModel::limpiarMensaje
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
    onReintentarOperacion: () -> Unit,
    onLimpiarMensaje: () -> Unit
) {
    var ofertaParaEditar by remember { mutableStateOf<Pair<Oferta, Boolean>?>(null) }
    var ofertaParaEliminar by remember { mutableStateOf<Oferta?>(null) }

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

    ofertaParaEliminar?.let { oferta ->
        DialogoConfirmacion(
            titulo = "Eliminar oferta en línea",
            textoConfirmar = "Eliminar",
            textoDescartar = "Cancelar",
            colorConfirmar = MaterialTheme.colorScheme.error,
            onConfirmar = {
                onEliminar(oferta)
                ofertaParaEliminar = null
            },
            onDescartar = { ofertaParaEliminar = null }
        ) {
            Text("¿Eliminar «${oferta.nombre}» de las ofertas en línea?")
        }
    }

    ListaOfertasNegocio(
        estado = estado,
        onSeleccion = onSeleccion,
        onReintentar = onReintentar,
        onPublicar = onPublicar,
        onEditar = { ofertaParaEditar = it to (estado.pestana == PestanaOfertas.EN_LINEA) },
        onEliminar = { ofertaParaEliminar = it },
        onReintentarOperacion = onReintentarOperacion,
        onLimpiarMensaje = onLimpiarMensaje
    )
}
