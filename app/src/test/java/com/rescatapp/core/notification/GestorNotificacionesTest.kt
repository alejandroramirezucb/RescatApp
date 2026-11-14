package com.rescatapp.core.notification

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.data.local.FakeNotificacionDao
import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GestorNotificacionesTest {

    private lateinit var fakeDao: FakeNotificacionDao
    private lateinit var repositorio: RepositorioNotificaciones
    private lateinit var notificadorSistema: FakeNotificadorSistema
    private lateinit var gestor: GestorNotificaciones

    private val pedidoPrueba = Pedido(
        id = 10,
        ofertaId = 2,
        nombreOferta = "Pan artesanal",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        precioPagado = 15.0,
        ahorro = 20.0,
        pesoKg = 1.5,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00",
        estado = EstadoPedido.RESERVADO
    )

    private val ofertaPrueba = Oferta(
        id = 5,
        nombre = "Torta de chocolate",
        comercio = "Panadería La Central",
        categoria = Categoria.POSTRES,
        descripcion = "Deliciosa torta",
        pesoKg = 0.8,
        precioNormal = 50.0,
        precioRescate = 25.0,
        cantidadDisponible = 0,
        horaRetiroDesde = "17:00",
        horaRetiroHasta = "19:00"
    )

    @Before
    fun preparar() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        fakeDao = FakeNotificacionDao()
        repositorio = RepositorioNotificaciones(fakeDao)
        notificadorSistema = FakeNotificadorSistema(context)
        gestor = GestorNotificaciones(repositorio, notificadorSistema)
    }

    @Test
    fun notificaPedidoEnPreparacionAlCliente() = runTest {
        gestor.notificarPedidoEnPreparacion(pedidoPrueba.copy(estado = EstadoPedido.PREPARANDO))

        val notificaciones = repositorio.observarPorRol(RolUsuario.CLIENTE).first()
        assertEquals(1, notificaciones.size)
        val notif = notificaciones.first()
        assertEquals(TipoNotificacion.PEDIDO_EN_PREPARACION, notif.tipo)
        assertTrue(notif.titulo.contains("preparación"))
        assertTrue(notif.mensaje.contains("Pan artesanal"))
        assertEquals(10, notif.pedidoId)

        assertEquals(1, notificadorSistema.notificacionesEmitidas.size)
        assertEquals(10, notificadorSistema.notificacionesEmitidas.first().id)
    }

    @Test
    fun notificaPedidoListoAlCliente() = runTest {
        gestor.notificarPedidoListo(pedidoPrueba.copy(estado = EstadoPedido.LISTO))

        val notificaciones = repositorio.observarPorRol(RolUsuario.CLIENTE).first()
        val notif = notificaciones.first()
        assertEquals(TipoNotificacion.PEDIDO_LISTO, notif.tipo)
        assertTrue(notif.titulo.contains("listo"))
        assertTrue(notif.mensaje.contains(pedidoPrueba.codigoRescate))
    }

    @Test
    fun notificaPedidoRecogidoAlCliente() = runTest {
        gestor.notificarPedidoRecogido(pedidoPrueba.copy(estado = EstadoPedido.RECOGIDO))

        val notificaciones = repositorio.observarPorRol(RolUsuario.CLIENTE).first()
        val notif = notificaciones.first()
        assertEquals(TipoNotificacion.PEDIDO_RECOGIDO, notif.tipo)
        assertTrue(notif.mensaje.contains("1.5 kg"))
    }

    @Test
    fun notificaCancelacionAClienteYVendedor() = runTest {
        gestor.notificarPedidoCanceladoCliente(pedidoPrueba)
        gestor.notificarPedidoCanceladoVendedor(pedidoPrueba)

        val notifCliente = repositorio.observarPorRol(RolUsuario.CLIENTE).first().first()
        val notifVendedor = repositorio.observarPorRol(RolUsuario.NEGOCIO).first().first()

        assertEquals(TipoNotificacion.PEDIDO_CANCELADO, notifCliente.tipo)
        assertEquals(TipoNotificacion.PEDIDO_CANCELADO, notifVendedor.tipo)
        assertTrue(notifVendedor.mensaje.contains("restituyó 1 unidad"))
    }

    @Test
    fun notificaNuevoPedidoAlVendedor() = runTest {
        gestor.notificarNuevoPedidoVendedor(pedidoPrueba)

        val notificaciones = repositorio.observarPorRol(RolUsuario.NEGOCIO).first()
        val notif = notificaciones.first()
        assertEquals(TipoNotificacion.NUEVO_PEDIDO_VENDEDOR, notif.tipo)
        assertTrue(notif.titulo.contains("Nuevo pedido"))
        assertTrue(notif.mensaje.contains("Pan artesanal"))
    }

    @Test
    fun notificaOfertaAgotadaYOfertaPublicadaAlVendedor() = runTest {
        gestor.notificarOfertaAgotada(ofertaPrueba)
        gestor.notificarOfertaPublicada(ofertaPrueba.copy(cantidadDisponible = 3))

        val notificaciones = repositorio.observarPorRol(RolUsuario.NEGOCIO).first()
        assertEquals(2, notificaciones.size)
        assertTrue(notificaciones.any { it.tipo == TipoNotificacion.OFERTA_AGOTADA })
        assertTrue(notificaciones.any { it.tipo == TipoNotificacion.OFERTA_PUBLICADA })
    }

    private class FakeNotificadorSistema(context: Context) : NotificadorSistemaAndroid(context) {
        data class Emision(
            val id: Int,
            val titulo: String,
            val mensaje: String,
            val tipo: TipoNotificacion
        )

        val notificacionesEmitidas = mutableListOf<Emision>()

        override fun mostrarNotificacion(
            id: Int,
            titulo: String,
            mensaje: String,
            tipo: TipoNotificacion
        ) {
            notificacionesEmitidas.add(Emision(id, titulo, mensaje, tipo))
        }

        override fun crearCanalesDeNotificacion() {}
    }
}
