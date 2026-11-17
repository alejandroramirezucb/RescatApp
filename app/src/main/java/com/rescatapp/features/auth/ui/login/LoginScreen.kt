package com.rescatapp.features.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.CampoConError
import com.rescatapp.features.auth.ui.BotonEnviarAuth
import com.rescatapp.features.auth.ui.EnlaceAuth
import com.rescatapp.features.auth.ui.MensajeErrorAuth
import com.rescatapp.features.auth.ui.PantallaAuth

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    onVolver: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val credenciales = estado.credenciales

    LaunchedEffect(estado.sesionIniciada) {
        if (estado.sesionIniciada) onLoginExitoso()
    }

    PantallaAuth(
        titulo = "Iniciar sesión",
        onVolver = onVolver,
        descripcion = "Encuentra tu próximo rescate o continúa gestionando tu comercio."
    ) {
        MensajeErrorAuth(estado.errorGeneral)
        CampoConError(
            etiqueta = "Correo electrónico",
            valor = credenciales.correo,
            onValorCambiado = { viewModel.actualizarCredenciales(credenciales.copy(correo = it)) },
            ejemplo = "ejemplo@correo.com",
            error = estado.errores.correo,
            tipoTeclado = KeyboardType.Email
        )
        CampoConError(
            etiqueta = "Contraseña",
            valor = credenciales.contrasena,
            onValorCambiado = {
                viewModel.actualizarCredenciales(credenciales.copy(contrasena = it))
            },
            ejemplo = "Mínimo 6 caracteres",
            error = estado.errores.contrasena,
            tipoTeclado = KeyboardType.Password
        )
        BotonEnviarAuth(
            texto = "Iniciar sesión",
            cargando = estado.cargando,
            habilitado = estado.botonHabilitado,
            onClick = viewModel::iniciarSesion
        )
        EnlaceAuth("¿No tienes una cuenta?", "Regístrate aquí", onIrARegistro)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Explora la demo", style = MaterialTheme.typography.titleSmall)
                Text(
                    text = "Prueba la experiencia de cliente o de negocio.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick = { viewModel.iniciarSesionDemo(esNegocio = false) },
                    enabled = !estado.cargando,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) {
                    Text("Entrar como cliente demo")
                }
                TextButton(
                    onClick = { viewModel.iniciarSesionDemo(esNegocio = true) },
                    enabled = !estado.cargando,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) {
                    Text("Entrar como negocio demo")
                }
            }
        }
    }
}
