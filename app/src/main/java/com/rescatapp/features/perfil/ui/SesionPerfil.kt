package com.rescatapp.features.perfil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun SesionPerfil(
    estado: PerfilUiState,
    onIrALogin: () -> Unit,
    onIrARegistro: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    TarjetaPerfil(titulo = "Cuenta y autenticación") {
        Text(
            text = if (estado.estaAutenticado) {
                "Sesión iniciada como ${estado.nombre} (${estado.correo})."
            } else {
                "Inicia sesión o regístrate para sincronizar tus pedidos y gestionar tu perfil."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (estado.estaAutenticado) {
                OutlinedButton(onClick = onCerrarSesion, modifier = Modifier.weight(1f)) {
                    Text("Cerrar sesión")
                }
                Button(onClick = onIrALogin, modifier = Modifier.weight(1f)) {
                    Text("Cambiar cuenta")
                }
            } else {
                Button(onClick = onIrALogin, modifier = Modifier.weight(1f)) {
                    Text("Iniciar sesión")
                }
                OutlinedButton(onClick = onIrARegistro, modifier = Modifier.weight(1f)) {
                    Text("Registrarse")
                }
            }
        }
    }
}
