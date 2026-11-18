package com.rescatapp.features.inicio.ui

import com.rescatapp.core.domain.Impacto
import com.rescatapp.features.inicio.domain.OfertasInicio

data class InicioUiState(
    val usuario: String = "Invitado",
    val direccion: String = "",
    val impacto: Impacto = Impacto(),
    val ofertas: OfertasInicio = OfertasInicio(),
    val notificacionesNoLeidas: Int = 0
)
