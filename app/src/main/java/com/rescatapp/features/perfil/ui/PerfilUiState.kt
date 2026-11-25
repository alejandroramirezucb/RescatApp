package com.rescatapp.features.perfil.ui

import com.rescatapp.core.domain.Impacto
import com.rescatapp.features.auth.domain.model.SesionUsuario

data class PerfilUiState(
    val impacto: Impacto = Impacto(),
    val nombre: String = "Invitado",
    val correo: String = "",
    val esNegocio: Boolean = false,
    val sesion: SesionUsuario? = null,
    val latitudTienda: Double? = null,
    val longitudTienda: Double? = null,
    val ubicacionPendiente: Boolean = false,
    val mensaje: String? = null
) {
    val inicial: String
        get() = nombre.take(1).uppercase()

    val estaAutenticado: Boolean
        get() = sesion != null
}
