package com.rescatapp.features.inicio.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo

internal enum class PestanaOfertas(val titulo: String) {
    ACTIVAS("Activas"),
    FINALIZADAS("Finalizadas")
}

@Composable
fun OfertasNegocioScreen(viewModel: OfertasNegocioViewModel = hiltViewModel()) {
    val ofertas by viewModel.ofertas.collectAsStateWithLifecycle()
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
                viewModel.actualizarOferta(actualizada)
                ofertaParaEditar = null
            },
            onDescartar = { ofertaParaEditar = null }
        )
    }

    ListaOfertasNegocio(
        pestana = pestana,
        onSeleccion = { pestana = it },
        ofertas = if (pestana == PestanaOfertas.ACTIVAS) ofertasActivas else ofertasFinalizadas,
        onEditar = { ofertaParaEditar = it }
    )
}
