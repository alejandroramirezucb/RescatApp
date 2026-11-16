package com.rescatapp.features.notificaciones.ui

import com.rescatapp.core.model.Notificacion

data class NotificacionesUiState(
    val notificaciones: List<Notificacion> = emptyList(),
    val conteoNoLeidas: Int = 0
)
