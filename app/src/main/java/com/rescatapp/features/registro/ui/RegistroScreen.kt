package com.rescatapp.features.registro.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.RegistroUiState

@Composable
fun RegistroScreen(onVolver: () -> Unit, viewModel: RegistroViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) {
            onVolver()
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
        TextButton(onClick = onVolver) { Text("Volver") }
        Text("Publicar oferta", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = campos.nombre,
            onValueChange = { onCampoCambiado(campos.copy(nombre = it)) },
            label = { Text("Nombre") },
            isError = errores.nombre != null,
            supportingText = { errores.nombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = campos.comercio,
            onValueChange = { onCampoCambiado(campos.copy(comercio = it)) },
            label = { Text("Comercio") },
            isError = errores.comercio != null,
            supportingText = { errores.comercio?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        Text("Categoría", style = MaterialTheme.typography.titleSmall)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Categoria.entries.forEach { categoria ->
                FilterChip(
                    selected = campos.categoria == categoria,
                    onClick = { onCampoCambiado(campos.copy(categoria = categoria)) },
                    label = { Text(categoria.etiqueta) }
                )
            }
        }
        errores.categoria?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(
            value = campos.descripcion,
            onValueChange = { onCampoCambiado(campos.copy(descripcion = it)) },
            label = { Text("Descripción") },
            isError = errores.descripcion != null,
            supportingText = { errores.descripcion?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        CampoNumerico("Peso en kg", campos.pesoKg, errores.pesoKg) {
            onCampoCambiado(campos.copy(pesoKg = it))
        }
        CampoNumerico(
            "Precio normal",
            campos.precioNormal,
            errores.precioNormal
        ) { onCampoCambiado(campos.copy(precioNormal = it)) }
        CampoNumerico(
            "Precio de rescate",
            campos.precioRescate,
            errores.precioRescate
        ) { onCampoCambiado(campos.copy(precioRescate = it)) }
        CampoNumerico(
            "Cantidad",
            campos.cantidadDisponible,
            errores.cantidadDisponible
        ) { onCampoCambiado(campos.copy(cantidadDisponible = it)) }
        CampoNumerico(
            "Retiro desde (HH:mm)",
            campos.horaRetiroDesde,
            errores.horaRetiroDesde
        ) { onCampoCambiado(campos.copy(horaRetiroDesde = it)) }
        CampoNumerico(
            "Retiro hasta (HH:mm)",
            campos.horaRetiroHasta,
            errores.horaRetiroHasta
        ) { onCampoCambiado(campos.copy(horaRetiroHasta = it)) }
        Button(
            onClick = onPublicar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar oferta")
        }
        estado.mensaje?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun CampoNumerico(
    etiqueta: String,
    valor: String,
    error: String?,
    onCambio: (String) -> Unit
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        modifier = Modifier.fillMaxWidth()
    )
}
