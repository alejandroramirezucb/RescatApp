package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.EfectoMensajeTemporal
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo

internal enum class PestanaOfertas(val titulo: String) {
    ACTIVAS("Activas"),
    FINALIZADAS("Finalizadas")
}

@Composable
fun OfertasNegocioScreen(viewModel: OfertasNegocioViewModel = hiltViewModel()) {
    val ofertas by viewModel.ofertas.collectAsStateWithLifecycle()
    val mensaje by viewModel.mensaje.collectAsStateWithLifecycle()
    val estadoSnackbar = remember { SnackbarHostState() }
    EfectoMensajeTemporal(mensaje, estadoSnackbar, viewModel::limpiarMensaje)
    val ofertasNegocio = remember(ofertas) {
        ofertas.filter { it.comercio == UsuarioDemo.NEGOCIO_DEMO }
    }
    val ofertasActivas = remember(ofertasNegocio) { ofertasNegocio.filter { !it.estaAgotada } }
    val ofertasFinalizadas = remember(ofertasNegocio) { ofertasNegocio.filter { it.estaAgotada } }

    var pestana by remember { mutableStateOf(PestanaOfertas.ACTIVAS) }
    var ofertaParaEditar by remember { mutableStateOf<Oferta?>(null) }

    ofertaParaEditar?.let { oferta ->
        DialogoEditarOferta(
            oferta = oferta,
            onGuardar = { actualizada ->
                viewModel.actualizarOferta(actualizada, onGuardada = { ofertaParaEditar = null })
            },
            onDescartar = { ofertaParaEditar = null }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ListaOfertasNegocio(
            pestana = pestana,
            onSeleccion = { pestana = it },
            ofertas = if (pestana == PestanaOfertas.ACTIVAS) ofertasActivas else ofertasFinalizadas,
            onEditar = { ofertaParaEditar = it }
        )
        SnackbarHost(estadoSnackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }
}
