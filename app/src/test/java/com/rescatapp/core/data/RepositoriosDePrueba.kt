package com.rescatapp.core.data

import com.rescatapp.core.data.local.FakeNotificacionDao
import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.local.SesionEntity
import com.rescatapp.core.data.remote.ImagenesOfertas
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario

const val USUARIO_PRUEBA = "usuario-1"

fun sesionDePrueba(rol: RolUsuario = RolUsuario.CLIENTE) = SesionEntity(
    usuarioId = USUARIO_PRUEBA,
    correo = "ana@correo.com",
    nombre = "Ana Pérez",
    rol = rol,
    nombreNegocio = if (rol == RolUsuario.NEGOCIO) "Panadería La Central" else null,
    token = "acceso",
    refreshToken = "renovacion",
    expira = Long.MAX_VALUE
)

object ImagenesFalsas : ImagenesOfertas {
    override suspend fun subir(ruta: String?, usuarioId: String, token: String) = ruta
}

fun crearAutenticacion(
    api: SupabaseApiFalsa = SupabaseApiFalsa(),
    sesion: SesionEntity? = sesionDePrueba()
) = RepositorioAutenticacion(api, FakeSesionDao(sesion))

fun crearRepositorioOfertas(
    ofertas: List<Oferta> = ofertasDeEjemplo,
    api: SupabaseApiFalsa = SupabaseApiFalsa(ofertas = ofertas),
    autenticacion: RepositorioAutenticacion = crearAutenticacion(api)
) = RepositorioOfertas(api, OfertaDaoFalso(api.ofertas), autenticacion, ImagenesFalsas)

fun crearRepositorioPedidos(
    pedidos: List<Pedido> = emptyList(),
    api: SupabaseApiFalsa = SupabaseApiFalsa(pedidos = pedidos),
    autenticacion: RepositorioAutenticacion = crearAutenticacion(api)
) = RepositorioPedidos(api, FakePedidoDao(api.pedidos), autenticacion)

fun crearRepositorioNotificaciones(
    notificaciones: List<Notificacion> = emptyList(),
    api: SupabaseApiFalsa = SupabaseApiFalsa(notificaciones = notificaciones),
    autenticacion: RepositorioAutenticacion = crearAutenticacion(api)
) = RepositorioNotificaciones(api, FakeNotificacionDao(api.notificaciones), autenticacion)
