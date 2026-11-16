package com.rescatapp.features.pago.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.components.BotonSecundario
import com.rescatapp.core.designsystem.theme.Rojo
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.features.pago.model.DatosQrPago
import com.rescatapp.features.pago.ui.components.CodigoQrCanvas
import kotlinx.coroutines.delay

@Composable
internal fun ContenidoPagoQr(
    datosQr: DatosQrPago,
    onSimularPago: (Boolean) -> Unit,
    onCancelar: () -> Unit
) {
    var segundosRestantes by remember(datosQr) { mutableIntStateOf(datosQr.expiracionSegundos) }
    LaunchedEffect(datosQr) {
        while (segundosRestantes > 0) {
            delay(1000L)
            segundosRestantes--
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Total a pagar: ${datosQr.monto.formatearDinero()}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        CodigoQrCanvas(datosQr.codigoQrPayload)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (segundosRestantes > 0) {
                    "Válido por: %02d:%02d min".format(
                        segundosRestantes / 60,
                        segundosRestantes % 60
                    )
                } else {
                    "Código QR expirado"
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontFeatureSettings = "tnum"),
                color = if (segundosRestantes <= 60) Rojo else Verde
            )
            Text(
                text = "Ref: ${datosQr.referenciaTransaccion}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "Escanea el código desde tu app bancaria o usa los botones de simulación:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        BotonPrincipal(
            texto = "Simular pago exitoso",
            onClick = { onSimularPago(true) },
            habilitado = segundosRestantes > 0
        )
        BotonSecundario(
            texto = "Simular pago rechazado",
            onClick = { onSimularPago(false) },
            colorTexto = Rojo
        )
        TextButton(onClick = onCancelar) {
            Text("Cancelar pago")
        }
    }
}
