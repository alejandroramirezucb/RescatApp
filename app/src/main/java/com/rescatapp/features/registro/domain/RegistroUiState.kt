package com.rescatapp.features.registro.domain

data class RegistroUiState(
    val campos: CamposRegistro = CamposRegistro(),
    val errores: ErroresRegistro = ErroresRegistro(),
    val guardadoExitoso: Boolean = false
)
