package com.rescatapp.core.data.local

import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import java.time.LocalDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class NotificacionEntityTest {

    @Test
    fun convierteNotificacionAEntidadYDominioSinPerderCampos() {
        val fecha = LocalDateTime.of(2026, 11, 14, 10, 30)
        val notificacion = Notificacion(
            id = "notif-123",
            titulo = "Pedido en preparación",
            mensaje = "El comercio está preparando tu comida",
            fechaHora = fecha,
            leida = false,
            tipo = TipoNotificacion.PEDIDO_EN_PREPARACION,
            rolDestino = RolUsuario.CLIENTE,
            pedidoId = 42,
            ofertaId = 15
        )

        val entidad = notificacion.toEntity()
        assertEquals("notif-123", entidad.id)
        assertEquals("Pedido en preparación", entidad.titulo)
        assertEquals("El comercio está preparando tu comida", entidad.mensaje)
        assertEquals(fecha, entidad.fechaHora)
        assertFalse(entidad.leida)
        assertEquals(TipoNotificacion.PEDIDO_EN_PREPARACION, entidad.tipo)
        assertEquals(RolUsuario.CLIENTE, entidad.rolDestino)
        assertEquals(42, entidad.pedidoId)
        assertEquals(15, entidad.ofertaId)

        val recuperada = entidad.toDomain()
        assertEquals(notificacion, recuperada)
    }
}
