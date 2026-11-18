package com.rescatapp.features.auth.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
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
    }
}
