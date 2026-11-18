package com.rescatapp.core.data.remote

import com.google.gson.annotations.SerializedName
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import java.time.OffsetDateTime
import java.time.ZoneId

data class NotificacionDto(
    val id: String,
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val leida: Boolean,
    @SerializedName("rol_destino") val rolDestino: String,
    @SerializedName("pedido_id") val pedidoId: Int?,
    @SerializedName("oferta_id") val ofertaId: Int?,
    @SerializedName("creado_at") val creadoAt: String
) {
    fun toDomain(): Notificacion = Notificacion(
        id = id,
        titulo = titulo,
        mensaje = mensaje,
        fechaHora = OffsetDateTime.parse(creadoAt)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime(),
        leida = leida,
        tipo = TipoNotificacion.valueOf(tipo),
        rolDestino = RolUsuario.valueOf(rolDestino),
        pedidoId = pedidoId,
        ofertaId = ofertaId
    )
}
