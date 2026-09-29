package com.rescatapp.features.perfil.domain

import com.rescatapp.core.model.ImpactoSemanal

data class PerfilUiState(
    val nombre: String = "Invitado",
    val correo: String = "invitado@example.com",
    val inicial: String = "I",
    val impacto: ImpactoSemanal = ImpactoSemanal(4, 102.0, 3.6),
    val rescates: Int = impacto.reservas,
    val ahorrado: Double = impacto.ahorro,
    val aprovechado: Double = impacto.pesoKg
)
