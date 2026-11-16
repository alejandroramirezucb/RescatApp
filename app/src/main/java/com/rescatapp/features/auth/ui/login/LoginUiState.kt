package com.rescatapp.features.auth.ui.login

import com.rescatapp.features.auth.domain.model.CredencialesLogin
import com.rescatapp.features.auth.domain.model.ErroresLogin

data class LoginUiState(
    val credenciales: CredencialesLogin = CredencialesLogin(),
    val errores: ErroresLogin = ErroresLogin(),
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val sesionIniciada: Boolean = false
) {
    val botonHabilitado: Boolean
        get() = !cargando && credenciales.correo.isNotBlank() &&
            credenciales.contrasena.isNotBlank()
}
