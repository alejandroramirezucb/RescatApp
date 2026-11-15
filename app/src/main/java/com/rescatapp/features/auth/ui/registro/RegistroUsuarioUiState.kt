package com.rescatapp.features.auth.ui.registro

import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.ErroresRegistro
import com.rescatapp.features.auth.domain.model.SesionUsuario

data class RegistroUsuarioUiState(
    val datos: DatosRegistro = DatosRegistro(),
    val contrasenaVisible: Boolean = false,
    val confirmarContrasenaVisible: Boolean = false,
    val errores: ErroresRegistro = ErroresRegistro(),
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val sesionExitosa: SesionUsuario? = null
) {
    val esNegocio: Boolean
        get() = datos.rol == RolUsuario.NEGOCIO

    val botonHabilitado: Boolean
        get() {
            if (cargando) return false
            val basicoValido = datos.nombre.isNotBlank() &&
                datos.correo.isNotBlank() &&
                datos.contrasena.isNotBlank() &&
                datos.confirmarContrasena.isNotBlank()
            return if (esNegocio) {
                basicoValido && datos.nombreNegocio.isNotBlank()
            } else {
                basicoValido
            }
        }
}
