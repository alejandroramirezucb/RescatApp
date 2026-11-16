package com.rescatapp.core.notification

import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import com.rescatapp.core.util.formatearDinero
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GestorNotificaciones @Inject constructor(
    private val repositorioNotificaciones: RepositorioNotificaciones,
    private val notificadorSistema: NotificadorSistemaAndroid
) {
    suspend fun notificarReservaConfirmadaCliente(pedido: Pedido) = notificarPedido(
        pedido,
        TipoNotificacion.PEDIDO_RESERVADO,
        RolUsuario.CLIENTE,
        "¡Reserva confirmada!",
        "Has reservado ${pedido.nombreOferta} en ${pedido.comercio}. " +
            "Retiro: ${pedido.horaRetiroDesde} - ${pedido.horaRetiroHasta}. " +
            "Código: ${pedido.codigoRescate}."
    )

    suspend fun notificarNuevoPedidoVendedor(pedido: Pedido) = notificarPedido(
        pedido,
        TipoNotificacion.NUEVO_PEDIDO_VENDEDOR,
        RolUsuario.NEGOCIO,
        "¡Nuevo pedido recibido!",
        "Nuevo pedido #${pedido.id}: ${pedido.nombreOferta} por " +
            "${pedido.precioPagado.formatearDinero()}. Código: ${pedido.codigoRescate}."
    )

    suspend fun notificarPedidoEnPreparacion(pedido: Pedido) = notificarPedido(
        pedido,
        TipoNotificacion.PEDIDO_EN_PREPARACION,
        RolUsuario.CLIENTE,
        "¡Tu pedido está en preparación!",
        "Tu pedido #${pedido.id} (${pedido.nombreOferta}) en ${pedido.comercio} " +
            "ya se está preparando."
    )

    suspend fun notificarPedidoListo(pedido: Pedido) = notificarPedido(
        pedido,
        TipoNotificacion.PEDIDO_LISTO,
        RolUsuario.CLIENTE,
        "¡Tu pedido está listo para retirar!",
        "Tu pedido #${pedido.id} (${pedido.nombreOferta}) está listo en ${pedido.comercio}. " +
            "Retíralo antes de las ${pedido.horaRetiroHasta}. Código: ${pedido.codigoRescate}."
    )

    suspend fun notificarPedidoRecogido(pedido: Pedido) = notificarPedido(
        pedido,
        TipoNotificacion.PEDIDO_RECOGIDO,
        RolUsuario.CLIENTE,
        "¡Pedido completado!",
        "¡Gracias por rescatar comida en ${pedido.comercio}! Salvaste ${pedido.pesoKg} kg " +
            "de alimentos y ahorraste ${pedido.ahorro.formatearDinero()}."
    )

    suspend fun notificarPedidoCanceladoCliente(pedido: Pedido) = notificarPedido(
        pedido,
        TipoNotificacion.PEDIDO_CANCELADO,
        RolUsuario.CLIENTE,
        "Pedido cancelado",
        "Tu pedido #${pedido.id} (${pedido.nombreOferta}) ha sido cancelado."
    )

    suspend fun notificarPedidoCanceladoVendedor(pedido: Pedido) = notificarPedido(
        pedido,
        TipoNotificacion.PEDIDO_CANCELADO,
        RolUsuario.NEGOCIO,
        "Pedido cancelado",
        "El pedido #${pedido.id} (${pedido.nombreOferta}) fue cancelado. " +
            "Se restituyó 1 unidad al inventario."
    )

    suspend fun notificarOfertaAgotada(oferta: Oferta) = notificar(
        Notificacion(
            titulo = "¡Oferta agotada!",
            mensaje = "Tu oferta '${oferta.nombre}' ha agotado todas sus unidades disponibles.",
            tipo = TipoNotificacion.OFERTA_AGOTADA,
            rolDestino = RolUsuario.NEGOCIO,
            ofertaId = oferta.id
        )
    )

    suspend fun notificarOfertaPublicada(oferta: Oferta) = notificar(
        Notificacion(
            titulo = "Oferta publicada con éxito",
            mensaje = "'${oferta.nombre}' ya está disponible en la app " +
                "(${oferta.cantidadDisponible} unidades a ${oferta.precioRescate.formatearDinero()}).",
            tipo = TipoNotificacion.OFERTA_PUBLICADA,
            rolDestino = RolUsuario.NEGOCIO,
            ofertaId = oferta.id
        )
    )

    private suspend fun notificarPedido(
        pedido: Pedido,
        tipo: TipoNotificacion,
        rolDestino: RolUsuario,
        titulo: String,
        mensaje: String
    ) = notificar(
        Notificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = tipo,
            rolDestino = rolDestino,
            pedidoId = pedido.id,
            ofertaId = pedido.ofertaId
        )
    )

    private suspend fun notificar(notificacion: Notificacion) {
        repositorioNotificaciones.agregar(notificacion)
        notificadorSistema.mostrarNotificacion(
            id = notificacion.id.hashCode(),
            titulo = notificacion.titulo,
            mensaje = notificacion.mensaje
        )
    }
}
