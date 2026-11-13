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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.core.designsystem.components.DialogoConfirmacion
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.aDecimalPositivoOrNull
import com.rescatapp.core.util.aHoraOrNull

@Composable
internal fun DialogoEditarOferta(
    oferta: Oferta,
    onGuardar: (Oferta) -> Unit,
    onDescartar: () -> Unit
) {
    val gestorFoco = LocalFocusManager.current
    var cantidad by remember { mutableStateOf(oferta.cantidadDisponible.toString()) }
    var precioRescate by remember { mutableStateOf(oferta.precioRescate.toString()) }
    var horaDesde by remember { mutableStateOf(oferta.horaRetiroDesde) }
    var horaHasta by remember { mutableStateOf(oferta.horaRetiroHasta) }
    var mostrarErrores by remember { mutableStateOf(false) }
    val cantidadValida = cantidad.trim().toIntOrNull()?.takeIf { it >= 0 }
    val precioValido = precioRescate.aDecimalPositivoOrNull()?.takeIf { it < oferta.precioNormal }
    val desde = horaDesde.aHoraOrNull()
    val hasta = horaHasta.aHoraOrNull()
    val horarioValido = desde != null && hasta != null && hasta.isAfter(desde)

    DialogoConfirmacion(
        titulo = "Editar oferta",
        textoConfirmar = "Guardar cambios",
        textoDescartar = "Cancelar",
        onConfirmar = {
            gestorFoco.clearFocus()
            mostrarErrores = true
            if (cantidadValida != null && precioValido != null && horarioValido) {
                onGuardar(
                    oferta.copy(
                        cantidadDisponible = cantidadValida,
                        precioRescate = precioValido,
                        horaRetiroDesde = horaDesde.trim(),
                        horaRetiroHasta = horaHasta.trim()
                    )
                )
            }
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
            if (mostrarErrores &&
                (cantidadValida == null || precioValido == null || !horarioValido)
            ) {
                Text(
                    "Ingresa unidades enteras (0 o más), un precio mayor a 0 y menor al normal, " +
                        "y horas HH:mm con fin posterior al inicio.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
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
