package com.rescatapp.core.model

import java.time.LocalDateTime
import java.util.UUID

data class Notificacion(
    val id: String = UUID.randomUUID().toString(),
    val titulo: String,
    val mensaje: String,
    val fechaHora: LocalDateTime = LocalDateTime.now(),
    val leida: Boolean = false,
    val tipo: TipoNotificacion,
    val rolDestino: RolUsuario,
    val pedidoId: Int? = null,
    val ofertaId: Int? = null
)
