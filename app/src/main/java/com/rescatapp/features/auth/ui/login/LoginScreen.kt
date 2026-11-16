package com.rescatapp.features.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BotonSecundario
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

    PantallaAuth(titulo = "Iniciar sesión", onVolver = onVolver) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("¡Bienvenido a RescatApp!", style = MaterialTheme.typography.headlineSmall)
            Text(
                text = "Inicia sesión para rescatar alimentos o gestionar tu comercio",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
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
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "O accede con una cuenta demo",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            BotonSecundario(
                texto = "Entrar como cliente demo",
                onClick = { viewModel.iniciarSesionDemo(esNegocio = false) },
                habilitado = !estado.cargando
            )
            BotonSecundario(
                texto = "Entrar como negocio demo",
                onClick = { viewModel.iniciarSesionDemo(esNegocio = true) },
                habilitado = !estado.cargando
            )
        }
        EnlaceAuth("¿No tienes una cuenta?", "Regístrate aquí", onIrARegistro)
    }
}
