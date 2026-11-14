package com.rescatapp.core.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import java.time.LocalDateTime

@Entity(
    tableName = "notificaciones",
    indices = [
        Index(value = ["rolDestino", "leida"]),
        Index(value = ["fechaHora"])
    ]
)
data class NotificacionEntity(
    @PrimaryKey
    val id: String,
    val titulo: String,
    val mensaje: String,
    val fechaHora: LocalDateTime,
    val leida: Boolean,
    val tipo: TipoNotificacion,
    val rolDestino: RolUsuario,
    val pedidoId: Int? = null,
    val ofertaId: Int? = null
) {
    fun toDomain(): Notificacion = Notificacion(
        id = id,
        titulo = titulo,
        mensaje = mensaje,
        fechaHora = fechaHora,
        leida = leida,
        tipo = tipo,
        rolDestino = rolDestino,
        pedidoId = pedidoId,
        ofertaId = ofertaId
    )
}

fun Notificacion.toEntity(): NotificacionEntity = NotificacionEntity(
    id = id,
    titulo = titulo,
    mensaje = mensaje,
    fechaHora = fechaHora,
    leida = leida,
    tipo = tipo,
    rolDestino = rolDestino,
    pedidoId = pedidoId,
    ofertaId = ofertaId
)
