package com.rescatapp.features.registro.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.ErroresRegistro
import kotlin.math.roundToInt

@Composable
fun FormularioOferta(
    campos: CamposRegistro,
    errores: ErroresRegistro,
    onCampoCambiado: (CamposRegistro) -> Unit
) {
    val normal = campos.precioNormal.replace(',', '.').toDoubleOrNull()
    val oferta = campos.precioRescate.replace(',', '.').toDoubleOrNull()
    val porcentajeAhorro = if (
        normal != null && oferta != null && normal > 0 && oferta > 0 && oferta < normal
    ) {
        ((1 - oferta / normal) * 100).roundToInt()
    } else {
        0
    }

    TarjetaAhorroCliente(porcentaje = porcentajeAhorro)

    CampoConError(
        etiqueta = "Nombre del pack",
        valor = campos.nombre,
        ejemplo = "Ej. Pack Sorpresa",
        onValorCambiado = { onCampoCambiado(campos.copy(nombre = it)) },
        error = errores.nombre
    )

    CampoConError(
        etiqueta = "Descripción",
        valor = campos.descripcion,
        ejemplo = "¿Qué incluye?",
        onValorCambiado = { onCampoCambiado(campos.copy(descripcion = it)) },
        error = errores.descripcion
    )

    SelectorCategoriaOferta(
        seleccionada = campos.categoria,
        error = errores.categoria,
        onSeleccion = { onCampoCambiado(campos.copy(categoria = it)) }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CampoConError(
            etiqueta = "Precio original",
            valor = campos.precioNormal,
            ejemplo = "45",
            tipoTeclado = KeyboardType.Decimal,
            onValorCambiado = { onCampoCambiado(campos.copy(precioNormal = it)) },
            error = errores.precioNormal,
            modifier = Modifier.weight(1f)
        )
        CampoConError(
            etiqueta = "Precio oferta",
            valor = campos.precioRescate,
            ejemplo = "25",
            tipoTeclado = KeyboardType.Decimal,
            destacarBordeNaranja = true,
            onValorCambiado = { onCampoCambiado(campos.copy(precioRescate = it)) },
            error = errores.precioRescate,
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CampoConError(
            etiqueta = "Cantidad",
            valor = campos.cantidadDisponible,
            ejemplo = "10",
            tipoTeclado = KeyboardType.Number,
            onValorCambiado = { onCampoCambiado(campos.copy(cantidadDisponible = it)) },
            error = errores.cantidadDisponible,
            modifier = Modifier.weight(1f)
        )
        CampoConError(
            etiqueta = "Hora inicio",
            valor = campos.horaRetiroDesde,
            ejemplo = "18:00",
            onValorCambiado = { onCampoCambiado(campos.copy(horaRetiroDesde = it)) },
            error = errores.horaRetiroDesde,
            modifier = Modifier.weight(1f)
        )
        CampoConError(
            etiqueta = "Hora fin",
            valor = campos.horaRetiroHasta,
            ejemplo = "20:00",
            onValorCambiado = { onCampoCambiado(campos.copy(horaRetiroHasta = it)) },
            error = errores.horaRetiroHasta,
            modifier = Modifier.weight(1f)
        )
    }

    SeccionFotoOferta()
}
