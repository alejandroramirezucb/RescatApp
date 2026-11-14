package com.rescatapp.features.registro.ui

import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.ErroresRegistro

data class RegistroUiState(
    val campos: CamposRegistro = CamposRegistro(),
    val errores: ErroresRegistro = ErroresRegistro(),
    val guardando: Boolean = false,
    val guardadoExitoso: Boolean = false,
    val mensaje: String? = null
)
