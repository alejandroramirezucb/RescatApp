package com.rescatapp.features.registro.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.VerdeRescat
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.RegistroUiState

@Composable
fun RegistroScreen(
    onVolver: () -> Unit,
    onPublicado: () -> Unit = onVolver,
    viewModel: RegistroViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) {
            onPublicado()
        }
    }

    RegistroContent(
        estado = estado,
        onVolver = onVolver,
        onCampoCambiado = viewModel::onCampoCambiado,
        onPublicar = viewModel::publicar
    )
}

@Composable
fun RegistroContent(
    estado: RegistroUiState,
    onVolver: () -> Unit,
    onCampoCambiado: (CamposRegistro) -> Unit,
    onPublicar: () -> Unit
) {
    val campos = estado.campos
    val errores = estado.errores

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onVolver) {
            Text("← Volver")
        }
        Text(
            text = "Publicar oferta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        CampoFormulario(
            etiqueta = "Nombre",
            valor = campos.nombre,
            error = errores.nombre,
            ejemplo = "Pack Salteñas",
            onCambio = { onCampoCambiado(campos.copy(nombre = it)) }
        )
        CampoFormulario(
            etiqueta = "Comercio",
            valor = campos.comercio,
            error = errores.comercio,
            ejemplo = "Panadería La Central",
            onCambio = { onCampoCambiado(campos.copy(comercio = it)) }
        )
        Text("Categoría", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Categoria.entries.forEach { categoria ->
                val seleccionada = campos.categoria == categoria
                FilterChip(
                    selected = seleccionada,
                    onClick = { onCampoCambiado(campos.copy(categoria = categoria)) },
                    label = { Text(categoria.etiqueta) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = VerdeRescat,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
        errores.categoria?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        CampoFormulario(
            etiqueta = "Descripción",
            valor = campos.descripcion,
            error = errores.descripcion,
            ejemplo = "Salteñas del día",
            onCambio = { onCampoCambiado(campos.copy(descripcion = it)) }
        )
        CampoFormulario(
            etiqueta = "Peso en kg",
            valor = campos.pesoKg,
            error = errores.pesoKg,
            ejemplo = "1.5",
            keyboardType = KeyboardType.Decimal,
            onCambio = { onCampoCambiado(campos.copy(pesoKg = it)) }
        )
        CampoFormulario(
            etiqueta = "Precio normal",
            valor = campos.precioNormal,
            error = errores.precioNormal,
            ejemplo = "40",
            keyboardType = KeyboardType.Decimal,
            onCambio = { onCampoCambiado(campos.copy(precioNormal = it)) }
        )
        CampoFormulario(
            etiqueta = "Precio de rescate",
            valor = campos.precioRescate,
            error = errores.precioRescate,
            ejemplo = "20",
            keyboardType = KeyboardType.Decimal,
            onCambio = { onCampoCambiado(campos.copy(precioRescate = it)) }
        )
        CampoFormulario(
            etiqueta = "Cantidad",
            valor = campos.cantidadDisponible,
            error = errores.cantidadDisponible,
            ejemplo = "4",
            keyboardType = KeyboardType.Number,
            onCambio = { onCampoCambiado(campos.copy(cantidadDisponible = it)) }
        )
        CampoFormulario(
            etiqueta = "Retiro desde (HH:mm)",
            valor = campos.horaRetiroDesde,
            error = errores.horaRetiroDesde,
            ejemplo = "18:00",
            onCambio = { onCampoCambiado(campos.copy(horaRetiroDesde = it)) }
        )
        CampoFormulario(
            etiqueta = "Retiro hasta (HH:mm)",
            valor = campos.horaRetiroHasta,
            error = errores.horaRetiroHasta,
            ejemplo = "18:00",
            onCambio = { onCampoCambiado(campos.copy(horaRetiroHasta = it)) }
        )
        Button(
            onClick = onPublicar,
            colors = ButtonDefaults.buttonColors(
                containerColor = NaranjaRescat,
                contentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar oferta")
        }
        estado.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun CampoFormulario(
    etiqueta: String,
    valor: String,
    error: String?,
    ejemplo: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    onCambio: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        placeholder = { Text(ejemplo) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
