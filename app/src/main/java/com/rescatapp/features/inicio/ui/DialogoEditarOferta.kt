package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.core.designsystem.components.DialogoConfirmacion
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Oferta

@Composable
internal fun DialogoEditarOferta(
    oferta: Oferta,
    onGuardar: (Oferta) -> Unit,
    onDescartar: () -> Unit
) {
    var cantidad by remember { mutableStateOf(oferta.cantidadDisponible.toString()) }
    var precioRescate by remember { mutableStateOf(oferta.precioRescate.toString()) }
    var horaDesde by remember { mutableStateOf(oferta.horaRetiroDesde) }
    var horaHasta by remember { mutableStateOf(oferta.horaRetiroHasta) }

    DialogoConfirmacion(
        titulo = "Editar oferta",
        textoConfirmar = "Guardar cambios",
        textoDescartar = "Cancelar",
        onConfirmar = {
            onGuardar(
                oferta.copy(
                    cantidadDisponible = cantidad.toIntOrNull() ?: oferta.cantidadDisponible,
                    precioRescate = precioRescate.toDoubleOrNull() ?: oferta.precioRescate,
                    horaRetiroDesde = horaDesde.trim(),
                    horaRetiroHasta = horaHasta.trim()
                )
            )
        },
        onDescartar = onDescartar
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = oferta.nombre,
                style = MaterialTheme.typography.titleMedium,
                color = Naranja
            )
            CampoConError(
                etiqueta = "Unidades disponibles",
                valor = cantidad,
                ejemplo = "10",
                tipoTeclado = KeyboardType.Number,
                onValorCambiado = { cantidad = it }
            )
            CampoConError(
                etiqueta = "Precio de rescate (Bs.)",
                valor = precioRescate,
                ejemplo = "25",
                tipoTeclado = KeyboardType.Decimal,
                destacarBordeNaranja = true,
                onValorCambiado = { precioRescate = it }
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CampoConError(
                    etiqueta = "Hora inicio",
                    valor = horaDesde,
                    ejemplo = "18:00",
                    modifier = Modifier.weight(1f),
                    onValorCambiado = { horaDesde = it }
                )
                CampoConError(
                    etiqueta = "Hora fin",
                    valor = horaHasta,
                    ejemplo = "20:00",
                    modifier = Modifier.weight(1f),
                    onValorCambiado = { horaHasta = it }
                )
            }
        }
    }
}
