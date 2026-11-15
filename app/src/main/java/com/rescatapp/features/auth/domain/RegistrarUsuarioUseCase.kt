package com.rescatapp.features.auth.domain

import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.auth.domain.model.DatosRegistro
import javax.inject.Inject

class RegistrarUsuarioUseCase @Inject constructor(
    private val repositorioAutenticacion: RepositorioAutenticacion,
    private val validarRegistroUsuario: ValidarRegistroUsuarioUseCase
) {

    suspend operator fun invoke(datos: DatosRegistro): ResultadoOperacion {
        val errores = validarRegistroUsuario(datos)
        if (errores.hayErrores) {
            val mensaje = errores.nombre
                ?: errores.nombreNegocio
                ?: errores.correo
                ?: errores.contrasena
                ?: errores.confirmarContrasena
                ?: "Por favor verifica los campos obligatorios."
            return ResultadoOperacion.Error(mensaje)
        }

        return repositorioAutenticacion.registrarUsuario(datos)
    }
}
