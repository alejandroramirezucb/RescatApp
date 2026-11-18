package com.rescatapp.features.auth.domain

import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.auth.domain.model.CredencialesLogin
import javax.inject.Inject

class IniciarSesionUseCase @Inject constructor(
    private val repositorioAutenticacion: RepositorioAutenticacion,
    private val validarLogin: ValidarLoginUseCase
) {

    suspend operator fun invoke(credenciales: CredencialesLogin): ResultadoOperacion {
        val errores = validarLogin(credenciales)
        if (errores.hayErrores) {
            val mensaje = errores.correo ?: errores.contrasena ?: "Credenciales inválidas"
            return ResultadoOperacion.Error(mensaje)
        }
        return repositorioAutenticacion.iniciarSesion(
            correo = credenciales.correo,
            contrasena = credenciales.contrasena
        )
    }
}
