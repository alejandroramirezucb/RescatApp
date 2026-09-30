package com.rescatapp.features.perfil.ui

import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.model.UsuarioDemo

data class PerfilUiState(
    val impacto: Impacto = Impacto(),
    val nombre: String = UsuarioDemo.NOMBRE,
    val correo: String = UsuarioDemo.CORREO,
    val inicial: String = UsuarioDemo.inicial,
    val esNegocio: Boolean = false
)
