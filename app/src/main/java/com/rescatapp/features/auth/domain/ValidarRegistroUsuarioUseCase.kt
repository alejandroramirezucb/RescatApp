package com.rescatapp.features.auth.domain

import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.ErroresRegistro
import javax.inject.Inject

class ValidarRegistroUsuarioUseCase @Inject constructor() {

    private val regexEmail = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    operator fun invoke(datos: DatosRegistro): ErroresRegistro {
        val errorNombre = when {
            datos.nombre.isBlank() -> "Ingresa tu nombre completo"
            datos.nombre.trim().length < 2 -> "El nombre debe tener al menos 2 caracteres"
            else -> null
        }

        val errorCorreo = when {
            datos.correo.isBlank() -> "Ingresa tu correo electrónico"
            !regexEmail.matches(datos.correo.trim()) -> "Ingresa un correo electrónico válido"
            else -> null
        }

        val errorContrasena = when {
            datos.contrasena.isBlank() -> "Ingresa una contraseña"
            datos.contrasena.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            else -> null
        }

        val errorConfirmacion = when {
            datos.confirmarContrasena.isBlank() -> "Confirma tu contraseña"
            datos.confirmarContrasena != datos.contrasena -> "Las contraseñas no coinciden"
            else -> null
        }

        val errorNombreNegocio = if (datos.rol == RolUsuario.NEGOCIO) {
            when {
                datos.nombreNegocio.isBlank() -> "Ingresa el nombre de tu comercio o negocio"

                datos.nombreNegocio.trim().length < 2 ->
                    "El nombre comercial debe tener al menos 2 caracteres"

                else -> null
            }
        } else {
            null
        }

        return ErroresRegistro(
            nombre = errorNombre,
            correo = errorCorreo,
            contrasena = errorContrasena,
            confirmarContrasena = errorConfirmacion,
            nombreNegocio = errorNombreNegocio
        )
    }
}
