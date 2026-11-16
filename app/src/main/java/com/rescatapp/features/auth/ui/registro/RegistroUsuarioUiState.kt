package com.rescatapp.features.auth.ui.registro

import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.ErroresRegistro

data class RegistroUsuarioUiState(
    val datos: DatosRegistro = DatosRegistro(),
    val errores: ErroresRegistro = ErroresRegistro(),
    val cargando: Boolean = false,
    val errorGeneral: String? = null,
    val sesionIniciada: Boolean = false
) {
    val esNegocio: Boolean
        get() = datos.rol == RolUsuario.NEGOCIO

    val botonHabilitado: Boolean
        get() = !cargando &&
            datos.nombre.isNotBlank() &&
            datos.correo.isNotBlank() &&
            datos.contrasena.isNotBlank() &&
            datos.confirmarContrasena.isNotBlank() &&
            (!esNegocio || datos.nombreNegocio.isNotBlank())
}
