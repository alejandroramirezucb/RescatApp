package com.rescatapp.features.auth.domain

import com.rescatapp.features.auth.domain.model.CredencialesLogin
import com.rescatapp.features.auth.domain.model.ErroresLogin
import javax.inject.Inject

class ValidarLoginUseCase @Inject constructor() {

    private val regexEmail = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    operator fun invoke(credenciales: CredencialesLogin): ErroresLogin {
        val errorCorreo = when {
            credenciales.correo.isBlank() -> "Ingresa tu correo electrónico"
            !regexEmail.matches(credenciales.correo.trim()) -> "Ingresa un correo electrónico válido"
            else -> null
        }

        val errorContrasena = when {
            credenciales.contrasena.isBlank() -> "Ingresa tu contraseña"
            credenciales.contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }

        return ErroresLogin(
            correo = errorCorreo,
            contrasena = errorContrasena
        )
    }
}
