package com.rescatapp.features.detalle.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.AvisoTemporal
import com.rescatapp.core.designsystem.components.BarraSuperior
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.features.pago.ui.PasarelaPagoDialog

@Composable
fun DetalleScreen(onVolver: () -> Unit, viewModel: DetalleViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    if (estado.pasarela.visible) {
        estado.oferta?.let { oferta ->
            PasarelaPagoDialog(
                oferta = oferta,
                estado = estado.pasarela,
                onSeleccionarMetodo = viewModel::seleccionarMetodoPago,
                onGenerarQr = viewModel::generarQrPago,
                onSimularPagoQr = viewModel::simularPagoQr,
                onPagarEnEfectivo = viewModel::reservar,
                onCompletarReserva = viewModel::reservar,
                onCerrar = viewModel::cerrarPasarelaPago
            )
        }
    }

    Scaffold(
        topBar = { BarraSuperior(titulo = "Detalle de la oferta", onVolver = onVolver) },
        bottomBar = {
            estado.oferta?.let { oferta ->
                BarraReservaOferta(oferta = oferta, onReservar = viewModel::abrirPasarelaPago)
            }
        },
        snackbarHost = { AvisoTemporal(estado.mensaje, viewModel::limpiarMensaje) }
    ) { espacioInterno ->
        val oferta = estado.oferta
        if (oferta == null) {
            Column(modifier = Modifier.padding(espacioInterno).padding(20.dp)) {
                EstadoVacio("Oferta no encontrada")
                BotonPrincipal(texto = "Volver", onClick = onVolver)
            }
        } else {
            InformacionOferta(
                oferta = oferta,
                modifier = Modifier.padding(espacioInterno)
            )
        }
    }
}
