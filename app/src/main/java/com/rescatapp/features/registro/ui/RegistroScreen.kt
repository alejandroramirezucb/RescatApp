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
        onNombreCambiado = viewModel::onNombreCambiado,
        onComercioCambiado = viewModel::onComercioCambiado,
        onCategoriaCambiada = viewModel::onCategoriaCambiada,
        onDescripcionCambiado = viewModel::onDescripcionCambiado,
        onPesoKgCambiado = viewModel::onPesoKgCambiado,
        onPrecioNormalCambiado = viewModel::onPrecioNormalCambiado,
        onPrecioRescateCambiado = viewModel::onPrecioRescateCambiado,
        onCantidadDisponibleCambiado = viewModel::onCantidadDisponibleCambiado,
        onHoraRetiroDesdeCambiado = viewModel::onHoraRetiroDesdeCambiado,
        onHoraRetiroHastaCambiado = viewModel::onHoraRetiroHastaCambiado,
        onPublicar = viewModel::publicar
    )
}

@Composable
fun RegistroContent(
    estado: RegistroUiState,
    onVolver: () -> Unit,
    onNombreCambiado: (String) -> Unit,
    onComercioCambiado: (String) -> Unit,
    onCategoriaCambiada: (Categoria) -> Unit,
    onDescripcionCambiado: (String) -> Unit,
    onPesoKgCambiado: (String) -> Unit,
    onPrecioNormalCambiado: (String) -> Unit,
    onPrecioRescateCambiado: (String) -> Unit,
    onCantidadDisponibleCambiado: (String) -> Unit,
    onHoraRetiroDesdeCambiado: (String) -> Unit,
    onHoraRetiroHastaCambiado: (String) -> Unit,
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
            onValueChange = onNombreCambiado,
            label = { Text("Nombre") },
            isError = errores.nombre != null,
            supportingText = { errores.nombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = campos.comercio,
            onValueChange = onComercioCambiado,
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
                    onClick = { onCategoriaCambiada(categoria) },
                    label = { Text(categoria.etiqueta) }
                )
            }
        }
        errores.categoria?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(
            value = campos.descripcion,
            onValueChange = onDescripcionCambiado,
            label = { Text("Descripción") },
            isError = errores.descripcion != null,
            supportingText = { errores.descripcion?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        CampoNumerico("Peso en kg", campos.pesoKg, errores.pesoKg, onPesoKgCambiado)
        CampoNumerico(
            "Precio normal",
            campos.precioNormal,
            errores.precioNormal,
            onPrecioNormalCambiado
        )
        CampoNumerico(
            "Precio de rescate",
            campos.precioRescate,
            errores.precioRescate,
            onPrecioRescateCambiado
        )
        CampoNumerico(
            "Cantidad",
            campos.cantidadDisponible,
            errores.cantidadDisponible,
            onCantidadDisponibleCambiado
        )
        CampoNumerico(
            "Retiro desde (HH:mm)",
            campos.horaRetiroDesde,
            errores.horaRetiroDesde,
            onHoraRetiroDesdeCambiado
        )
        CampoNumerico(
            "Retiro hasta (HH:mm)",
            campos.horaRetiroHasta,
            errores.horaRetiroHasta,
            onHoraRetiroHastaCambiado
        )
        Button(
            onClick = onPublicar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar oferta")
        }
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
