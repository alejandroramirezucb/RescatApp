package com.rescatapp.features.perfil.domain

import com.rescatapp.core.domain.Impacto

data class PerfilUiState(
    val nombre: String = "Invitado",
    val correo: String = "invitado@example.com",
    val inicial: String = "I",
    val impacto: Impacto = Impacto(),
    val rescates: Int = impacto.rescates,
    val ahorrado: Double = impacto.ahorrado,
    val aprovechado: Double = impacto.kgAprovechados
)
