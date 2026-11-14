package com.rescatapp.features.pago.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.rescatapp.core.designsystem.theme.Rojo
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.designsystem.theme.VerdeSuave
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.features.pago.model.ComprobantePago
import com.rescatapp.features.pago.model.DatosQrPago
import com.rescatapp.features.pago.model.EstadoPago
import com.rescatapp.features.pago.model.MetodoPago
import com.rescatapp.features.pago.ui.components.CodigoQrCanvas
import com.rescatapp.features.pago.ui.components.SelectorMetodoPago
import com.rescatapp.features.pago.ui.components.TarjetaResumenPago
import kotlinx.coroutines.delay

@Composable
fun PasarelaPagoDialog(
    oferta: Oferta,
    datosQr: DatosQrPago?,
    estadoPago: EstadoPago,
    comprobante: ComprobantePago?,
    metodoSeleccionado: MetodoPago,
    errorPago: String?,
    onSeleccionarMetodo: (MetodoPago) -> Unit,
    onGenerarQr: () -> Unit,
    onSimularPagoQr: (simularExito: Boolean) -> Unit,
    onConfirmarPagoEfectivo: () -> Unit,
    onCompletarReservaTrasPago: () -> Unit,
    onDescartar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = {
            if (estadoPago != EstadoPago.PROCESANDO) {
                onDescartar()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .widthIn(max = 440.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                EncabezadoPasarela(
                    estadoPago = estadoPago,
                    onCerrar = onDescartar
                )

                when (estadoPago) {
                    EstadoPago.PENDIENTE -> {
                        if (datosQr == null) {
                            ContenidoSeleccionMetodo(
                                oferta = oferta,
                                metodoSeleccionado = metodoSeleccionado,
                                onSeleccionarMetodo = onSeleccionarMetodo,
                                onContinuar = {
                                    if (metodoSeleccionado == MetodoPago.QR_SIMPLE) {
                                        onGenerarQr()
                                    } else {
                                        onConfirmarPagoEfectivo()
                                    }
                                }
                            )
                        } else {
                            ContenidoPagoQr(
                                datosQr = datosQr,
                                onSimularPago = onSimularPagoQr,
                                onCambiarMetodo = onGenerarQr,
                                onCancelar = onDescartar
                            )
                        }
                    }

                    EstadoPago.PROCESANDO -> {
                        ContenidoProcesandoPago()
                    }

                    EstadoPago.APROBADO -> {
                        comprobante?.let {
                            ContenidoPagoAprobado(
                                comprobante = it,
                                onFinalizar = onCompletarReservaTrasPago
                            )
                        }
                    }

                    EstadoPago.RECHAZADO -> {
                        ContenidoPagoRechazado(
                            motivo = errorPago ?: "Transacción no autorizada por el banco.",
                            onReintentarQr = onGenerarQr,
                            onCambiarAEfectivo = {
                                onSeleccionarMetodo(MetodoPago.EFECTIVO)
                                onConfirmarPagoEfectivo()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EncabezadoPasarela(estadoPago: EstadoPago, onCerrar: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Pasarela de Pago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (estadoPago != EstadoPago.PROCESANDO) {
            IconButton(onClick = onCerrar) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar pasarela de pago"
                )
            }
        }
    }
}

@Composable
private fun ContenidoSeleccionMetodo(
    oferta: Oferta,
    metodoSeleccionado: MetodoPago,
    onSeleccionarMetodo: (MetodoPago) -> Unit,
    onContinuar: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Resumen de tu reserva",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        TarjetaResumenPago(oferta = oferta)

        Text(
            text = "Elige tu método de pago",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        SelectorMetodoPago(
            metodoSeleccionado = metodoSeleccionado,
            onSeleccionarMetodo = onSeleccionarMetodo
        )

        Button(
            onClick = onContinuar,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = if (metodoSeleccionado == MetodoPago.QR_SIMPLE) {
                    "Continuar al Pago QR"
                } else {
                    "Confirmar Reserva (Pago en local)"
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ContenidoPagoQr(
    datosQr: DatosQrPago,
    onSimularPago: (simularExito: Boolean) -> Unit,
    onCambiarMetodo: () -> Unit,
    onCancelar: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var segundosRestantes by remember(datosQr) {
        mutableIntStateOf(datosQr.expiracionSegundos)
    }

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
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        CodigoQrCanvas(payload = datosQr.codigoQrPayload, tamano = 210.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = if (segundosRestantes <= 60) Rojo else Verde,
                modifier = Modifier.size(16.dp)
            )
            val minutos = segundosRestantes / 60
            val segundos = segundosRestantes % 60
            val textoTiempo = String.format("%02d:%02d", minutos, segundos)
            Text(
                text = if (segundosRestantes > 0) {
                    "Válido por: $textoTiempo min"
                } else {
                    "Código QR expirado"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (segundosRestantes <= 60) Rojo else Verde,
                fontWeight = FontWeight.SemiBold
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ref: ${datosQr.referenciaTransaccion}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(datosQr.referenciaTransaccion))
                        Toast.makeText(
                            context,
                            "Referencia copiada al portapapeles",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copiar referencia",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Text(
            text = "Escanea el código desde tu app bancaria (BCP, BNB, Banco Unión) o " +
                "usa los botones de simulación a continuación:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = { onSimularPago(true) },
            enabled = segundosRestantes > 0,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Verde),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Simular Pago Exitoso (Banco)", fontWeight = FontWeight.Bold)
        }

        OutlinedButton(
            onClick = { onSimularPago(false) },
            modifier = Modifier.fillMaxWidth().height(44.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Simular Pago Rechazado", color = Rojo)
        }

        TextButton(onClick = onCancelar) {
            Text("Cancelar o cambiar método")
        }
    }
}

@Composable
private fun ContenidoProcesandoPago() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(54.dp),
            strokeWidth = 4.dp
        )
        Text(
            text = "Procesando pago con la red bancaria...",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Validando acreditación interbancaria. Por favor espera un momento.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ContenidoPagoAprobado(comprobante: ComprobantePago, onFinalizar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(VerdeSuave),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Verde,
                modifier = Modifier.size(44.dp)
            )
        }

        Text(
            text = "¡Pago Aprobado con Éxito!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Verde
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilaComprobante("Comprobante N°", comprobante.numeroTransaccion)
                FilaComprobante("Referencia", comprobante.referencia)
                FilaComprobante("Monto", comprobante.monto.formatearDinero())
                FilaComprobante("Comercio", comprobante.comercio)
                FilaComprobante("Método", comprobante.metodoPago.titulo)
                FilaComprobante("Fecha", comprobante.fechaHora)
            }
        }

        Button(
            onClick = onFinalizar,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Completar Reserva y Ver Pedido", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun FilaComprobante(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ContenidoPagoRechazado(
    motivo: String,
    onReintentarQr: () -> Unit,
    onCambiarAEfectivo: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEE2E2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = Rojo,
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = "Pago No Procesado",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Rojo
        )

        Text(
            text = motivo,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onReintentarQr,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Generar Nuevo QR y Reintentar")
        }

        OutlinedButton(
            onClick = onCambiarAEfectivo,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Pagar en Efectivo al Retirar")
        }
    }
}
