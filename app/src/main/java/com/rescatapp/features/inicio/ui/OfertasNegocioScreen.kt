package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    var pestana by remember { mutableStateOf(PestanaOfertas.ACTIVAS) }

    Column(modifier = Modifier.fillMaxSize()) {
        EncabezadoOfertasNegocio()
        SelectorOfertasNegocio(pestana) { pestana = it }
        ListaOfertasNegocio(
            ofertas = if (pestana == PestanaOfertas.ACTIVAS) ofertasNegocio else emptyList(),
            pestana = pestana
        )
    }
}
