package com.rescatapp.features.pago.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.components.BotonSecundario
import com.rescatapp.core.designsystem.theme.Rojo
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.features.pago.model.ComprobantePago
import com.rescatapp.features.pago.model.MetodoPago

@Composable
internal fun PagoProcesando() {
    CircularProgressIndicator()
    Text("Procesando pago con la red bancaria...", style = MaterialTheme.typography.titleMedium)
}

@Composable
internal fun PagoAprobado(comprobante: ComprobantePago, onFinalizar: () -> Unit) {
    TituloResultado(Icons.Default.CheckCircle, "Pago aprobado", Verde)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        FilaComprobante("Comprobante N°", comprobante.numeroTransaccion)
        FilaComprobante("Referencia", comprobante.referencia)
        FilaComprobante("Monto", comprobante.monto.formatearDinero())
        FilaComprobante("Comercio", comprobante.comercio)
        FilaComprobante("Método", MetodoPago.QR_SIMPLE.titulo)
        FilaComprobante("Fecha", comprobante.fechaHora)
    }
    BotonPrincipal(texto = "Completar reserva", onClick = onFinalizar)
}

@Composable
internal fun PagoRechazado(
    motivo: String,
    onReintentar: () -> Unit,
    onPagarEnEfectivo: () -> Unit
) {
    TituloResultado(Icons.Default.ErrorOutline, "Pago no procesado", Rojo)
    Text(text = motivo, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
    BotonPrincipal(texto = "Generar nuevo QR y reintentar", onClick = onReintentar)
    BotonSecundario(texto = "Pagar en efectivo al retirar", onClick = onPagarEnEfectivo)
}

@Composable
private fun TituloResultado(icono: ImageVector, titulo: String, color: Color) {
    Icon(icono, contentDescription = null, tint = color, modifier = Modifier.size(56.dp))
    Text(titulo, style = MaterialTheme.typography.titleLarge, color = color)
}

@Composable
private fun FilaComprobante(etiqueta: String, valor: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(valor, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
    }
}
