package com.rescatapp.features.registro.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.core.designsystem.components.ChipSeleccionable
import com.rescatapp.core.designsystem.components.icono
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.ErroresRegistro

@Composable
fun FormularioOferta(
    campos: CamposRegistro,
    errores: ErroresRegistro,
    onCampoCambiado: (CamposRegistro) -> Unit
) {
    CampoConError(
        etiqueta = "Nombre de la oferta",
        valor = campos.nombre,
        onValorCambiado = { onCampoCambiado(campos.copy(nombre = it)) },
        error = errores.nombre,
        ejemplo = "Pack Salteñas"
    )
    CampoConError(
        etiqueta = "Comercio",
        valor = campos.comercio,
        onValorCambiado = { onCampoCambiado(campos.copy(comercio = it)) },
        error = errores.comercio,
        ejemplo = "Panadería La Central"
    )
    SelectorCategoria(
        seleccionada = campos.categoria,
        error = errores.categoria,
        onSeleccion = { onCampoCambiado(campos.copy(categoria = it)) }
    )
    CampoConError(
        etiqueta = "Descripción",
        valor = campos.descripcion,
        onValorCambiado = { onCampoCambiado(campos.copy(descripcion = it)) },
        error = errores.descripcion,
        ejemplo = "Salteñas del día"
    )
    CampoConError(
        etiqueta = "Peso en kg",
        valor = campos.pesoKg,
        onValorCambiado = { onCampoCambiado(campos.copy(pesoKg = it)) },
        error = errores.pesoKg,
        ejemplo = "1,5",
        tipoTeclado = KeyboardType.Decimal
    )
    CampoConError(
        etiqueta = "Precio normal (Bs)",
        valor = campos.precioNormal,
        onValorCambiado = { onCampoCambiado(campos.copy(precioNormal = it)) },
        error = errores.precioNormal,
        ejemplo = "40",
        tipoTeclado = KeyboardType.Decimal
    )
    CampoConError(
        etiqueta = "Precio de rescate (Bs)",
        valor = campos.precioRescate,
        onValorCambiado = { onCampoCambiado(campos.copy(precioRescate = it)) },
        error = errores.precioRescate,
        ejemplo = "20",
        tipoTeclado = KeyboardType.Decimal
    )
    CampoConError(
        etiqueta = "Cantidad disponible",
        valor = campos.cantidadDisponible,
        onValorCambiado = { onCampoCambiado(campos.copy(cantidadDisponible = it)) },
        error = errores.cantidadDisponible,
        ejemplo = "4",
        tipoTeclado = KeyboardType.Number
    )
    CampoConError(
        etiqueta = "Retiro desde",
        valor = campos.horaRetiroDesde,
        onValorCambiado = { onCampoCambiado(campos.copy(horaRetiroDesde = it)) },
        error = errores.horaRetiroDesde,
        ejemplo = "18:00"
    )
    CampoConError(
        etiqueta = "Retiro hasta",
        valor = campos.horaRetiroHasta,
        onValorCambiado = { onCampoCambiado(campos.copy(horaRetiroHasta = it)) },
        error = errores.horaRetiroHasta,
        ejemplo = "20:00"
    )
}

@Composable
private fun SelectorCategoria(
    seleccionada: Categoria?,
    error: String?,
    onSeleccion: (Categoria) -> Unit
) {
    Text(text = "Categoría", style = MaterialTheme.typography.titleSmall)
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Categoria.entries.forEach { categoria ->
            ChipSeleccionable(
                etiqueta = categoria.etiqueta,
                seleccionado = categoria == seleccionada,
                onClick = { onSeleccion(categoria) },
                icono = categoria.icono
            )
        }
    }
    error?.let {
        Text(
            text = it,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}
