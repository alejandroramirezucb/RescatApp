package com.rescatapp.features.auth.ui.login

import com.rescatapp.features.auth.domain.model.CredencialesLogin
import com.rescatapp.features.auth.domain.model.ErroresLogin
import com.rescatapp.features.auth.domain.model.SesionUsuario

data class LoginUiState(
    val credenciales: CredencialesLogin = CredencialesLogin(),
    val contrasenaVisible: Boolean = false,
    val errores: ErroresLogin = ErroresLogin(),
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val sesionExitosa: SesionUsuario? = null
) {
    val botonHabilitado: Boolean
        get() = !cargando &&
            credenciales.correo.isNotBlank() &&
            credenciales.contrasena.isNotBlank()
}
