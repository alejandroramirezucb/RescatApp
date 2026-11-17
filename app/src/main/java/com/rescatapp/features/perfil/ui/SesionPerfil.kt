package com.rescatapp.features.perfil.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.designsystem.components.BotonSecundario

@Composable
internal fun SesionPerfil(
    estado: PerfilUiState,
    onIrALogin: () -> Unit,
    onIrARegistro: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    TarjetaPerfil(titulo = if (estado.estaAutenticado) "Tu sesión" else "Accede a tu cuenta") {
        Text(
            text = if (estado.estaAutenticado) {
                "Tienes una sesión iniciada con ${estado.correo}."
            } else {
                "Estás explorando con un perfil de demostración. " +
                    "Puedes iniciar sesión o crear una cuenta."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (estado.estaAutenticado) {
            BotonPrincipal(texto = "Cambiar cuenta", onClick = onIrALogin)
            BotonSecundario(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                colorTexto = MaterialTheme.colorScheme.error
            )
        } else {
            BotonPrincipal(texto = "Iniciar sesión", onClick = onIrALogin)
            BotonSecundario(texto = "Registrarse", onClick = onIrARegistro)
        }
    }
}
