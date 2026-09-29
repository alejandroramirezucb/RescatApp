package com.rescatapp.features.inicio.ui

import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.inicio.domain.OfertasInicio

data class InicioUiState(
    val usuario: String = UsuarioDemo.NOMBRE,
    val impacto: Impacto = Impacto(),
    val ofertas: OfertasInicio = OfertasInicio()
)
