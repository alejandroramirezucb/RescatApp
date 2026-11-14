package com.rescatapp.features.notificaciones.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.data.local.FakeNotificacionDao
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NotificacionesViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private lateinit var fakeDao: FakeNotificacionDao
    private lateinit var repositorio: RepositorioNotificaciones

    @Before
    fun preparar() {
        UsuarioDemo.cambiarRol(RolUsuario.CLIENTE)
        fakeDao = FakeNotificacionDao()
        repositorio = RepositorioNotificaciones(fakeDao)
    }

    @Test
    fun estadoInicialVacio() = runTest {
        val viewModel = NotificacionesViewModel(repositorio)
        observarDuranteLaPrueba(viewModel.uiState)

        val estado = viewModel.uiState.value
        assertTrue(estado.notificaciones.isEmpty())
        assertEquals(0, estado.conteoNoLeidas)
        assertEquals(RolUsuario.CLIENTE, estado.rol)
    }

    @Test
    fun muestraNotificacionesYPermiteMarcarComoLeida() = runTest {
        val notif = Notificacion(
            id = "notif-1",
            titulo = "En preparación",
            mensaje = "Tu pedido se está preparando",
            tipo = TipoNotificacion.PEDIDO_EN_PREPARACION,
            rolDestino = RolUsuario.CLIENTE,
            leida = false
        )
        repositorio.agregar(notif)

        val viewModel = NotificacionesViewModel(repositorio)
        observarDuranteLaPrueba(viewModel.uiState)

        assertEquals(1, viewModel.uiState.value.notificaciones.size)
        assertEquals(1, viewModel.uiState.value.conteoNoLeidas)

        viewModel.marcarComoLeida("notif-1")
        assertEquals(0, viewModel.uiState.value.conteoNoLeidas)
        assertTrue(viewModel.uiState.value.notificaciones.first().leida)
    }

    @Test
    fun limpiarTodasVaciaLaLista() = runTest {
        repositorio.agregar(
            Notificacion(
                id = "notif-2",
                titulo = "Listo",
                mensaje = "Pasa a retirar",
                tipo = TipoNotificacion.PEDIDO_LISTO,
                rolDestino = RolUsuario.CLIENTE
            )
        )

        val viewModel = NotificacionesViewModel(repositorio)
        observarDuranteLaPrueba(viewModel.uiState)

        assertEquals(1, viewModel.uiState.value.notificaciones.size)

        viewModel.limpiarTodas()
        assertTrue(viewModel.uiState.value.notificaciones.isEmpty())
    }
}
