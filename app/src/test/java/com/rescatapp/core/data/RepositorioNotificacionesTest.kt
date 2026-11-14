package com.rescatapp.core.data

import com.rescatapp.core.data.local.FakeNotificacionDao
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositorioNotificacionesTest {

    private lateinit var fakeDao: FakeNotificacionDao
    private lateinit var repositorio: RepositorioNotificaciones

    @Before
    fun preparar() {
        fakeDao = FakeNotificacionDao()
        repositorio = RepositorioNotificaciones(fakeDao)
    }

    @Test
    fun agregaNotificacionesYSeparaPorRol() = runTest {
        val notifCliente = Notificacion(
            id = "c1",
            titulo = "Preparando",
            mensaje = "Pedido 1",
            tipo = TipoNotificacion.PEDIDO_EN_PREPARACION,
            rolDestino = RolUsuario.CLIENTE
        )
        val notifNegocio = Notificacion(
            id = "n1",
            titulo = "Nuevo pedido",
            mensaje = "Pedido recibido",
            tipo = TipoNotificacion.NUEVO_PEDIDO_VENDEDOR,
            rolDestino = RolUsuario.NEGOCIO
        )

        repositorio.agregar(notifCliente)
        repositorio.agregar(notifNegocio)

        val listaCliente = repositorio.observarPorRol(RolUsuario.CLIENTE).first()
        val listaNegocio = repositorio.observarPorRol(RolUsuario.NEGOCIO).first()

        assertEquals(1, listaCliente.size)
        assertEquals("c1", listaCliente.first().id)
        assertEquals(1, listaNegocio.size)
        assertEquals("n1", listaNegocio.first().id)
    }

    @Test
    fun cuentaNoLeidasYPermiteMarcarComoLeida() = runTest {
        val notif1 = Notificacion(
            id = "1",
            titulo = "A",
            mensaje = "B",
            tipo = TipoNotificacion.PEDIDO_LISTO,
            rolDestino = RolUsuario.CLIENTE,
            leida = false
        )
        val notif2 = Notificacion(
            id = "2",
            titulo = "C",
            mensaje = "D",
            tipo = TipoNotificacion.PEDIDO_RECOGIDO,
            rolDestino = RolUsuario.CLIENTE,
            leida = false
        )

        repositorio.agregar(notif1)
        repositorio.agregar(notif2)

        assertEquals(2, repositorio.contarNoLeidas(RolUsuario.CLIENTE).first())

        repositorio.marcarComoLeida("1")
        assertEquals(1, repositorio.contarNoLeidas(RolUsuario.CLIENTE).first())

        repositorio.marcarTodasComoLeidas(RolUsuario.CLIENTE)
        assertEquals(0, repositorio.contarNoLeidas(RolUsuario.CLIENTE).first())
    }

    @Test
    fun eliminaNotificacionesIndividualesYPorRol() = runTest {
        repositorio.agregar(
            Notificacion(
                id = "x1",
                titulo = "A",
                mensaje = "B",
                tipo = TipoNotificacion.SISTEMA,
                rolDestino = RolUsuario.CLIENTE
            )
        )
        repositorio.agregar(
            Notificacion(
                id = "x2",
                titulo = "C",
                mensaje = "D",
                tipo = TipoNotificacion.SISTEMA,
                rolDestino = RolUsuario.CLIENTE
            )
        )

        repositorio.eliminar("x1")
        assertEquals(1, repositorio.observarPorRol(RolUsuario.CLIENTE).first().size)

        repositorio.eliminarTodas(RolUsuario.CLIENTE)
        assertTrue(repositorio.observarPorRol(RolUsuario.CLIENTE).first().isEmpty())
    }
}
