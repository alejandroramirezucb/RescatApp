package com.rescatapp.features.pago.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.model.Oferta
import com.rescatapp.features.pago.model.EstadoPago
import com.rescatapp.features.pago.model.EstadoPasarela
import com.rescatapp.features.pago.model.MetodoPago
import com.rescatapp.features.pago.ui.components.SelectorMetodoPago
import com.rescatapp.features.pago.ui.components.TarjetaResumenPago

@Composable
fun PasarelaPagoDialog(
    oferta: Oferta,
    estado: EstadoPasarela,
    onSeleccionarMetodo: (MetodoPago) -> Unit,
    onGenerarQr: () -> Unit,
    onSimularPagoQr: (Boolean) -> Unit,
    onPagarEnEfectivo: () -> Unit,
    onCompletarReserva: () -> Unit,
    onCerrar: () -> Unit
) {
    val procesando = estado.estado == EstadoPago.PROCESANDO
    Dialog(onDismissRequest = { if (!procesando) onCerrar() }) {
        Surface(shape = RoundedCornerShape(20.dp)) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pasarela de pago", style = MaterialTheme.typography.titleMedium)
                    if (!procesando) {
                        IconButton(onClick = onCerrar) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                }
                when (estado.estado) {
                    EstadoPago.PENDIENTE -> if (estado.datosQr == null) {
                        SeleccionMetodo(
                            oferta = oferta,
                            metodo = estado.metodo,
                            onSeleccionarMetodo = onSeleccionarMetodo,
                            onContinuar = {
                                if (estado.metodo == MetodoPago.QR_SIMPLE) {
                                    onGenerarQr()
                                } else {
                                    onPagarEnEfectivo()
                                }
                            }
                        )
                    } else {
                        ContenidoPagoQr(estado.datosQr, onSimularPagoQr, onCerrar)
                    }

                    EstadoPago.PROCESANDO -> PagoProcesando()

                    EstadoPago.APROBADO -> estado.comprobante?.let {
                        PagoAprobado(it, onCompletarReserva)
                    }

                    EstadoPago.RECHAZADO -> PagoRechazado(
                        motivo = estado.error.orEmpty(),
                        onReintentar = onGenerarQr,
                        onPagarEnEfectivo = onPagarEnEfectivo
                    )
                }
            }
        }
    }
}

@Composable
private fun SeleccionMetodo(
    oferta: Oferta,
    metodo: MetodoPago,
    onSeleccionarMetodo: (MetodoPago) -> Unit,
    onContinuar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TarjetaResumenPago(oferta)
        Text("Elige tu método de pago", style = MaterialTheme.typography.labelLarge)
        SelectorMetodoPago(metodo, onSeleccionarMetodo)
        BotonPrincipal(
            texto = if (metodo == MetodoPago.QR_SIMPLE) {
                "Continuar al pago QR"
            } else {
                "Confirmar reserva (pago en local)"
            },
            onClick = onContinuar
        )
    }
}
