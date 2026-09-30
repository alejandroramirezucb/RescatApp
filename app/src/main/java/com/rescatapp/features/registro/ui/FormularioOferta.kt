package com.rescatapp.features.registro.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.model.Categoria
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

    CampoEntrada(
        etiqueta = "Nombre del pack",
        valor = campos.nombre,
        placeholder = "Ej. Pack Sorpresa",
        onValorCambiado = { onCampoCambiado(campos.copy(nombre = it)) },
        error = errores.nombre
    )

    CampoEntrada(
        etiqueta = "Descripción",
        valor = campos.descripcion,
        placeholder = "¿Qué incluye?",
        onValorCambiado = { onCampoCambiado(campos.copy(descripcion = it)) },
        error = errores.descripcion
    )

    SelectorCategoriaDropdown(
        seleccionada = campos.categoria,
        error = errores.categoria,
        onSeleccion = { onCampoCambiado(campos.copy(categoria = it)) }
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CampoEntrada(
            etiqueta = "Precio original",
            valor = campos.precioNormal,
            placeholder = "45",
            tipoTeclado = KeyboardType.Decimal,
            onValorCambiado = { onCampoCambiado(campos.copy(precioNormal = it)) },
            error = errores.precioNormal,
            modifier = Modifier.weight(1f)
        )
        CampoEntrada(
            etiqueta = "Precio oferta",
            valor = campos.precioRescate,
            placeholder = "25",
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
        CampoEntrada(
            etiqueta = "Cantidad",
            valor = campos.cantidadDisponible,
            placeholder = "10",
            tipoTeclado = KeyboardType.Number,
            onValorCambiado = { onCampoCambiado(campos.copy(cantidadDisponible = it)) },
            error = errores.cantidadDisponible,
            modifier = Modifier.weight(1f)
        )
        CampoEntrada(
            etiqueta = "Hora inicio",
            valor = campos.horaRetiroDesde,
            placeholder = "18:00",
            onValorCambiado = { onCampoCambiado(campos.copy(horaRetiroDesde = it)) },
            error = errores.horaRetiroDesde,
            modifier = Modifier.weight(1f)
        )
        CampoEntrada(
            etiqueta = "Hora fin",
            valor = campos.horaRetiroHasta,
            placeholder = "20:00",
            onValorCambiado = { onCampoCambiado(campos.copy(horaRetiroHasta = it)) },
            error = errores.horaRetiroHasta,
            modifier = Modifier.weight(1f)
        )
    }

    SeccionAgregarFoto()
}

@Composable
private fun TarjetaAhorroCliente(porcentaje: Int) {
    Surface(
        color = Color(0xFFFFF8F0),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "El cliente ahorra:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (porcentaje > 0) "$porcentaje%" else "--%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Naranja
            )
        }
    }
}

@Composable
private fun CampoEntrada(
    etiqueta: String,
    valor: String,
    onValorCambiado: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    destacarBordeNaranja: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            },
            isError = error != null,
            keyboardOptions = KeyboardOptions(keyboardType = tipoTeclado),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.15f
                ),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.15f
                ),
                focusedBorderColor = if (destacarBordeNaranja) {
                    Naranja
                } else {
                    MaterialTheme.colorScheme.primary
                },
                unfocusedBorderColor = if (destacarBordeNaranja) {
                    Naranja
                } else {
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun SelectorCategoriaDropdown(
    seleccionada: Categoria?,
    error: String?,
    onSeleccion: (Categoria) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Categoría",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box {
            Surface(
                onClick = { expandido = true },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                border = BorderStroke(
                    1.dp,
                    if (error != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = seleccionada?.etiqueta ?: "Panadería",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Desplegar categorías",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            DropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                Categoria.entries.forEach { categoria ->
                    DropdownMenuItem(
                        text = { Text(categoria.etiqueta) },
                        onClick = {
                            onSeleccion(categoria)
                            expandido = false
                        }
                    )
                }
            }
        }
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun SeccionAgregarFoto() {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Agregar foto",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
