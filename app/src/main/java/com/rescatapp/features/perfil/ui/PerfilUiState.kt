package com.rescatapp.features.perfil.ui

import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.auth.domain.model.SesionUsuario

data class PerfilUiState(
    val impacto: Impacto = Impacto(),
    val nombre: String = UsuarioDemo.NOMBRE,
    val correo: String = UsuarioDemo.CORREO,
    val esNegocio: Boolean = false,
    val sesion: SesionUsuario? = null
) {
    val inicial: String
        get() = nombre.take(1).uppercase()

    val estaAutenticado: Boolean
        get() = sesion != null
}
