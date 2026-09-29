package com.rescatapp.features.detalle.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearDisponibilidad
import com.rescatapp.core.util.formatearHorario

@Composable
fun DetalleScreen(oferta: Oferta?, onVolver: () -> Unit, onReservar: () -> Boolean) {
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onVolver) { Text("Volver") }
        if (oferta == null) {
            Text("Oferta no encontrada", style = MaterialTheme.typography.titleMedium)
        } else {
            Text(oferta.nombre, style = MaterialTheme.typography.headlineMedium)
            Text(oferta.comercio, style = MaterialTheme.typography.titleMedium)
            Text(oferta.descripcion)
            Text("${oferta.precioRescate.formatearDinero()} · -${oferta.porcentajeDescuento}%")
            Text(oferta.cantidadDisponible.formatearDisponibilidad())
            Text(formatearHorario(oferta.horaRetiroDesde, oferta.horaRetiroHasta))
            Button(
                onClick = {
                    mensaje =
                        if (onReservar()) {
                            "Reservaste ${oferta.nombre}"
                        } else {
                            "Esta oferta está agotada"
                        }
                },
                enabled = !oferta.estaAgotada
            ) {
                Text("Reservar")
            }
            if (mensaje.isNotEmpty()) Text(mensaje)
        }
    }
}
