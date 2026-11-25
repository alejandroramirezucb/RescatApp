package com.rescatapp.features.carrito.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.AvisoTemporal
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.components.EstadoVacio
import com.rescatapp.features.pago.ui.PasarelaPagoDialog

@Composable
fun CarritoScreen(onExplorar: () -> Unit, viewModel: CarritoViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    if (estado.pasarela.visible) {
        PasarelaPagoDialog(
            carrito = estado.carrito,
            estado = estado.pasarela,
            onSeleccionarMetodo = viewModel::seleccionarMetodoPago,
            onGenerarQr = viewModel::generarQrPago,
            onSimularPagoQr = viewModel::simularPagoQr,
            onPagarEnEfectivo = viewModel::confirmar,
            onCompletarReserva = viewModel::confirmar,
            onCerrar = viewModel::cerrarPasarelaPago
        )
    }

    Scaffold(
        bottomBar = {
            if (estado.carrito.items.isNotEmpty()) {
                ResumenCarrito(
                    carrito = estado.carrito,
                    confirmando = estado.confirmando,
                    onPagar = viewModel::abrirPasarelaPago
                )
            }
        },
        snackbarHost = { AvisoTemporal(estado.mensaje, viewModel::limpiarMensaje) }
    ) { espacioInterno ->
        LazyColumn(
            modifier = Modifier.padding(espacioInterno).fillMaxWidth(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text(
                    text = "Mi carrito",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth()
                )
            }
            if (estado.carrito.items.isEmpty()) {
                item {
                    Column(modifier = Modifier.widthIn(max = 600.dp)) {
                        EstadoVacio("Tu carrito está vacío")
                        BotonPrincipal(texto = "Explorar ofertas", onClick = onExplorar)
                    }
                }
            }
            items(estado.carrito.items, key = { it.oferta.id }) { item ->
                TarjetaItemCarrito(
                    item = item,
                    onCambiarCantidad = { viewModel.cambiarCantidad(item.oferta, it) },
                    onEliminar = { viewModel.eliminar(item.oferta.id) },
                    modifier = Modifier.widthIn(max = 600.dp)
                )
            }
        }
    }
}
