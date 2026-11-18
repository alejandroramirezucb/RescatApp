package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun CampoConError(
    etiqueta: String,
    valor: String,
    onValorCambiado: (String) -> Unit,
    ejemplo: String,
    modifier: Modifier = Modifier,
    error: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    destacarBordeNaranja: Boolean = false
) {
    val esContrasena = tipoTeclado == KeyboardType.Password
    var contrasenaVisible by rememberSaveable { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (error != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = valor,
            onValueChange = onValorCambiado,
            placeholder = {
                Text(
                    text = ejemplo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            isError = error != null,
            keyboardOptions = KeyboardOptions(
                keyboardType = tipoTeclado,
                imeAction = ImeAction.Next
            ),
            visualTransformation = if (esContrasena && !contrasenaVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (esContrasena) {
                {
                    IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                        Icon(
                            imageVector = if (contrasenaVisible) {
                                Icons.Outlined.VisibilityOff
                            } else {
                                Icons.Outlined.Visibility
                            },
                            contentDescription = if (contrasenaVisible) {
                                "Ocultar contraseña"
                            } else {
                                "Mostrar contraseña"
                            }
                        )
                    }
                }
            } else {
                null
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyLarge,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                errorContainerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (destacarBordeNaranja) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                },
                errorBorderColor = MaterialTheme.colorScheme.error,
                cursorColor = MaterialTheme.colorScheme.primary,
                errorCursorColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        )
        if (error != null) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 4.dp).semantics {
                    liveRegion = LiveRegionMode.Polite
                }
            )
        }
    }
}
