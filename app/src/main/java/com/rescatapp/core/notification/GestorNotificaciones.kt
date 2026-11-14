package com.rescatapp.core.notification

import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GestorNotificaciones @Inject constructor(
    private val repositorioNotificaciones: RepositorioNotificaciones,
    private val notificadorSistema: NotificadorSistemaAndroid
) {
    suspend fun notificarPedidoEnPreparacion(pedido: Pedido) {
        val titulo = "👨‍🍳 ¡Tu pedido está en preparación!"
        val mensaje =
            "Tu pedido #${pedido.id} (${pedido.nombreOferta}) en ${pedido.comercio} ya se está preparando."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_EN_PREPARACION,
            rolDestino = RolUsuario.CLIENTE,
            pedidoId = pedido.id,
            ofertaId = pedido.ofertaId
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = pedido.id,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_EN_PREPARACION
        )
    }

    suspend fun notificarPedidoListo(pedido: Pedido) {
        val titulo = "🛍️ ¡Tu pedido está listo para retirar!"
        val mensaje =
            "Tu pedido #${pedido.id} (${pedido.nombreOferta}) está listo en ${pedido.comercio}." +
                " Retíralo antes de las ${pedido.horaRetiroHasta}. Código: ${pedido.codigoRescate}."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_LISTO,
            rolDestino = RolUsuario.CLIENTE,
            pedidoId = pedido.id,
            ofertaId = pedido.ofertaId
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = pedido.id,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_LISTO
        )
    }

    suspend fun notificarPedidoRecogido(pedido: Pedido) {
        val ahorroFormateado = "%.2f".format(pedido.ahorro)
        val titulo = "🌱 ¡Pedido completado!"
        val mensaje =
            "¡Gracias por rescatar comida en ${pedido.comercio}! " +
                "Salvaste ${pedido.pesoKg} kg de alimentos y ahorraste Bs. $ahorroFormateado."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_RECOGIDO,
            rolDestino = RolUsuario.CLIENTE,
            pedidoId = pedido.id,
            ofertaId = pedido.ofertaId
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = pedido.id,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_RECOGIDO
        )
    }

    suspend fun notificarPedidoCanceladoCliente(pedido: Pedido) {
        val titulo = "❌ Pedido cancelado"
        val mensaje = "Tu pedido #${pedido.id} (${pedido.nombreOferta}) ha sido cancelado."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_CANCELADO,
            rolDestino = RolUsuario.CLIENTE,
            pedidoId = pedido.id,
            ofertaId = pedido.ofertaId
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = pedido.id,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_CANCELADO
        )
    }

    suspend fun notificarPedidoCanceladoVendedor(pedido: Pedido) {
        val titulo = "⚠️ Pedido cancelado"
        val mensaje =
            "El pedido #${pedido.id} (${pedido.nombreOferta}) fue cancelado. Se restituyó 1 unidad al inventario."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_CANCELADO,
            rolDestino = RolUsuario.NEGOCIO,
            pedidoId = pedido.id,
            ofertaId = pedido.ofertaId
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = pedido.id + 10000,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_CANCELADO
        )
    }

    suspend fun notificarNuevoPedidoVendedor(pedido: Pedido) {
        val precioFormateado = "%.2f".format(pedido.precioPagado)
        val titulo = "🔔 ¡Nuevo pedido recibido!"
        val mensaje =
            "Nuevo pedido #${pedido.id}: ${pedido.nombreOferta} por Bs. $precioFormateado." +
                " Código: ${pedido.codigoRescate}."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.NUEVO_PEDIDO_VENDEDOR,
            rolDestino = RolUsuario.NEGOCIO,
            pedidoId = pedido.id,
            ofertaId = pedido.ofertaId
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = pedido.id + 20000,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.NUEVO_PEDIDO_VENDEDOR
        )
    }

    suspend fun notificarReservaConfirmadaCliente(pedido: Pedido) {
        val titulo = "🎉 ¡Reserva confirmada!"
        val mensaje =
            "Has reservado ${pedido.nombreOferta} en ${pedido.comercio}." +
                " Retiro: ${pedido.horaRetiroDesde} - ${pedido.horaRetiroHasta}." +
                " Código: ${pedido.codigoRescate}."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_RESERVADO,
            rolDestino = RolUsuario.CLIENTE,
            pedidoId = pedido.id,
            ofertaId = pedido.ofertaId
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = pedido.id,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.PEDIDO_RESERVADO
        )
    }

    suspend fun notificarOfertaAgotada(oferta: Oferta) {
        val titulo = "📦 ¡Oferta agotada!"
        val mensaje = "Tu oferta '${oferta.nombre}' ha agotado todas sus unidades disponibles."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.OFERTA_AGOTADA,
            rolDestino = RolUsuario.NEGOCIO,
            ofertaId = oferta.id
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = oferta.id + 30000,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.OFERTA_AGOTADA
        )
    }

    suspend fun notificarOfertaPublicada(oferta: Oferta) {
        val precioFormateado = "%.2f".format(oferta.precioRescate)
        val titulo = "✨ Oferta publicada con éxito"
        val mensaje =
            "'${oferta.nombre}' ya está disponible en la app" +
                " (${oferta.cantidadDisponible} unidades a Bs. $precioFormateado)."
        val notificacion = Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.OFERTA_PUBLICADA,
            rolDestino = RolUsuario.NEGOCIO,
            ofertaId = oferta.id
        )
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = oferta.id + 40000,
            titulo = titulo,
            mensaje = mensaje,
            tipo = TipoNotificacion.OFERTA_PUBLICADA
        )
    }
}
