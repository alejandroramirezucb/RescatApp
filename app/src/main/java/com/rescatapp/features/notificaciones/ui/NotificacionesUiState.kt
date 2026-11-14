package com.rescatapp.features.notificaciones.ui

import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario

data class NotificacionesUiState(
    val notificaciones: List<Notificacion> = emptyList(),
    val conteoNoLeidas: Int = 0,
    val rol: RolUsuario = RolUsuario.CLIENTE
)
