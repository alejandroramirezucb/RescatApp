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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.ErroresRegistro
import com.rescatapp.features.registro.domain.ValidarOfertaUseCase

@Composable
fun RegistroScreen(onVolver: () -> Unit, onPublicar: (CamposRegistro) -> Boolean) {
    var campos by remember { mutableStateOf(CamposRegistro()) }
    var errores by remember { mutableStateOf(ErroresRegistro()) }
    var mensaje by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onVolver) { Text("Volver") }
        Text("Publicar oferta", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = campos.nombre,
            onValueChange = { campos = campos.copy(nombre = it) },
            label = { Text("Nombre") },
            isError = errores.nombre != null,
            supportingText = { errores.nombre?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = campos.comercio,
            onValueChange = { campos = campos.copy(comercio = it) },
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
                    onClick = { campos = campos.copy(categoria = categoria) },
                    label = { Text(categoria.etiqueta) }
                )
            }
        }
        errores.categoria?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        OutlinedTextField(
            value = campos.descripcion,
            onValueChange = { campos = campos.copy(descripcion = it) },
            label = { Text("Descripción") },
            isError = errores.descripcion != null,
            supportingText = { errores.descripcion?.let { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        CampoNumerico("Peso en kg", campos.pesoKg, errores.pesoKg) {
            campos = campos.copy(pesoKg = it)
        }
        CampoNumerico("Precio normal", campos.precioNormal, errores.precioNormal) {
            campos = campos.copy(precioNormal = it)
        }
        CampoNumerico("Precio de rescate", campos.precioRescate, errores.precioRescate) {
            campos = campos.copy(precioRescate = it)
        }
        CampoNumerico("Cantidad", campos.cantidadDisponible, errores.cantidadDisponible) {
            campos = campos.copy(cantidadDisponible = it)
        }
        CampoNumerico("Retiro desde (HH:mm)", campos.horaRetiroDesde, errores.horaRetiroDesde) {
            campos = campos.copy(horaRetiroDesde = it)
        }
        CampoNumerico("Retiro hasta (HH:mm)", campos.horaRetiroHasta, errores.horaRetiroHasta) {
            campos = campos.copy(horaRetiroHasta = it)
        }
        Button(
            onClick = {
                errores = ValidarOfertaUseCase()(campos)
                if (!errores.hayErrores) {
                    if (!onPublicar(campos)) mensaje = "No se pudo publicar la oferta"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Publicar oferta")
        }
        if (mensaje.isNotEmpty()) Text(mensaje, color = MaterialTheme.colorScheme.error)
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
