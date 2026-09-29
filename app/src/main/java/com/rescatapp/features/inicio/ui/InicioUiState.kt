package com.rescatapp.features.inicio.ui

import com.rescatapp.core.domain.Impacto
import com.rescatapp.features.inicio.domain.OfertasInicio

data class InicioUiState(
    val impacto: Impacto = Impacto(),
    val ofertas: OfertasInicio = OfertasInicio()
)
