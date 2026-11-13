package com.rescatapp.features.detalle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.DialogoConfirmacion
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearHorario

@Composable
internal fun DialogoReserva(oferta: Oferta, onConfirmar: () -> Unit, onDescartar: () -> Unit) {
    DialogoConfirmacion(
        titulo = "Confirmar reserva",
        textoConfirmar = "Confirmar",
        onConfirmar = onConfirmar,
        onDescartar = onDescartar
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "¿Deseas reservar \"${oferta.nombre}\" por " +
                    "${oferta.precioRescate.formatearDinero()}?",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Comercio: ${oferta.comercio}\n" +
                    "Horario de retiro: " +
                    formatearHorario(oferta.horaRetiroDesde, oferta.horaRetiroHasta),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
