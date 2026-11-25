package com.rescatapp.core.data

import com.rescatapp.core.data.remote.NegocioOfertaDto
import com.rescatapp.core.data.remote.NotificacionDto
import com.rescatapp.core.data.remote.OfertaDto
import com.rescatapp.core.data.remote.PedidoDto
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import java.time.ZoneId
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response

fun Oferta.conCampos(campos: Map<String, Any?>) = copy(
    nombre = campos["nombre"] as String,
    categoria = Categoria.valueOf(campos["categoria"] as String),
    descripcion = campos["descripcion"] as String,
    pesoKg = campos["peso_kg"] as Double,
    precioNormal = campos["precio_normal"] as Double,
    precioRescate = campos["precio_rescate"] as Double,
    cantidadDisponible = campos["cantidad_disponible"] as Int,
    horaRetiroDesde = campos["hora_retiro_desde"] as String,
    horaRetiroHasta = campos["hora_retiro_hasta"] as String,
    imagenUri = campos["imagen_url"] as String?
)

fun errorServidor(mensaje: String) = HttpException(
    Response.error<Any>(400, """{"message":"$mensaje"}""".toResponseBody())
)

fun Oferta.aDto() = OfertaDto(
    id, nombre, categoria.name, descripcion, pesoKg, precioNormal, precioRescate,
    cantidadDisponible, fechaRetiro.toString(), horaRetiroDesde, horaRetiroHasta, imagenUri,
    estadoPublicacion, NegocioOfertaDto(comercio, propietarioId)
)

fun Pedido.aDto() = PedidoDto(
    id, ofertaId, nombreOferta, comercio, categoria.name, precioPagado, ahorro, pesoKg,
    fechaRetiro.toString(), horaRetiroDesde, horaRetiroHasta, imagenUri, estado.name,
    codigoRescate,
    fecha.atStartOfDay(
        ZoneId.systemDefault()
    ).toOffsetDateTime().toString(),
    clienteNombre,
    clienteTelefono,
    estadoPagoSimulado.name
)

fun Notificacion.aDto() = NotificacionDto(
    id, titulo, mensaje, tipo.name, leida, rolDestino.name, pedidoId, ofertaId,
    fechaHora.atZone(ZoneId.systemDefault()).toOffsetDateTime().toString()
)

fun Oferta.crearPedido(idPedido: Int, estado: EstadoPedido = EstadoPedido.RESERVADO) = Pedido(
    id = idPedido,
    ofertaId = id,
    nombreOferta = nombre,
    comercio = comercio,
    categoria = categoria,
    precioPagado = precioRescate,
    ahorro = ahorroPorUnidad,
    pesoKg = pesoKg,
    horaRetiroDesde = horaRetiroDesde,
    horaRetiroHasta = horaRetiroHasta,
    estado = estado,
    imagenUri = imagenUri
)
