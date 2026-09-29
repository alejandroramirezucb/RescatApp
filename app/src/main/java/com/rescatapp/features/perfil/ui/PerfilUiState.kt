package com.rescatapp.features.perfil.ui

data class PerfilUiState(
    val nombre: String = "Invitado",
    val correo: String = "invitado@example.com",
    val inicial: String = "I",
    val rescates: Int = 0,
    val ahorrado: Double = 0.0,
    val aprovechado: Double = 0.0
)
