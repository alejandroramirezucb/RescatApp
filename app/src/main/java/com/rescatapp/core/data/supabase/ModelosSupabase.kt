package com.rescatapp.core.data.supabase

import com.google.gson.JsonObject
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId

internal val zonaBolivia: ZoneId = ZoneId.of("America/La_Paz")
internal fun JsonObject.texto(nombre: String): String =
    get(nombre)?.takeUnless { it.isJsonNull }?.asString.orEmpty()
internal fun JsonObject.entero(nombre: String): Int = get(nombre).asInt
internal fun JsonObject.decimal(nombre: String): Double = get(nombre).asDouble

internal fun JsonObject.aOferta(): Oferta = Oferta(
    id = entero("id"),
    nombre = texto("nombre"),
    comercio = getAsJsonObject("negocios")?.texto("nombre").orEmpty(),
    categoria = Categoria.valueOf(texto("categoria")),
    descripcion = texto("descripcion"),
    pesoKg = decimal("peso_kg"),
    precioNormal = decimal("precio_normal"),
    precioRescate = decimal("precio_rescate"),
    cantidadDisponible = entero("cantidad_disponible"),
    horaRetiroDesde = texto("hora_retiro_desde").take(5),
    horaRetiroHasta = texto("hora_retiro_hasta").take(5),
    imagenUri = texto("imagen_url").ifBlank { null },
    propietarioId = getAsJsonObject("negocios")?.texto("propietario_id"),
    estadoPublicacion = texto("estado"),
    fechaRetiro = LocalDate.parse(texto("fecha_retiro"))
)

internal fun JsonObject.aPedido(): Pedido = Pedido(
    id = entero("id"),
    ofertaId = entero("oferta_id"),
    nombreOferta = texto("nombre_oferta"),
    comercio = texto("comercio"),
    categoria = Categoria.valueOf(texto("categoria")),
    precioPagado = decimal("precio_pagado"),
    ahorro = decimal("ahorro"),
    pesoKg = decimal("peso_kg"),
    horaRetiroDesde = texto("hora_retiro_desde").take(5),
    horaRetiroHasta = texto("hora_retiro_hasta").take(5),
    estado = EstadoPedido.valueOf(texto("estado")),
    imagenUri = texto("imagen_url").ifBlank { null },
    fecha = OffsetDateTime.parse(texto("creado_at")).atZoneSameInstant(zonaBolivia).toLocalDate(),
    codigoServidor = texto("codigo_rescate"),
    fechaRetiro = LocalDate.parse(texto("fecha_retiro"))
)

internal fun JsonObject.aNotificacion(): Notificacion = Notificacion(
    id = texto("id"), titulo = texto("titulo"), mensaje = texto("mensaje"),
    fechaHora = OffsetDateTime.parse(
        texto("creado_at")
    ).atZoneSameInstant(zonaBolivia).toLocalDateTime(),
    leida = get("leida").asBoolean,
    tipo = TipoNotificacion.valueOf(texto("tipo")),
    rolDestino = RolUsuario.valueOf(texto("rol_destino")),
    pedidoId = get("pedido_id")?.takeUnless { it.isJsonNull }?.asInt,
    ofertaId = get("oferta_id")?.takeUnless { it.isJsonNull }?.asInt
)
