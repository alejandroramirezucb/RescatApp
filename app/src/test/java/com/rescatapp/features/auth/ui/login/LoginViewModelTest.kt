package com.rescatapp.features.auth.ui.login

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.local.FakeUsuarioDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
import com.rescatapp.features.auth.domain.IniciarSesionUseCase
import com.rescatapp.features.auth.domain.ValidarLoginUseCase
import com.rescatapp.features.auth.domain.model.SesionUsuario
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private lateinit var usuarioDao: FakeUsuarioDao
    private lateinit var sesionDao: FakeSesionDao
    private lateinit var repositorio: RepositorioAutenticacion
    private lateinit var iniciarSesionUseCase: IniciarSesionUseCase
    private lateinit var validarLoginUseCase: ValidarLoginUseCase
    private lateinit var viewModel: LoginViewModel

    @Before
    fun preparar() {
        usuarioDao = FakeUsuarioDao(UsuariosDeEjemplo.todos.map { it.toEntity() })
        sesionDao = FakeSesionDao()
        repositorio = RepositorioAutenticacion(usuarioDao, sesionDao)
        validarLoginUseCase = ValidarLoginUseCase()
        iniciarSesionUseCase = IniciarSesionUseCase(repositorio, validarLoginUseCase)
        viewModel = LoginViewModel(iniciarSesionUseCase, validarLoginUseCase)
    }

    @Test
    fun estadoInicialEsVacioYDeshabilitado() {
        val estado = viewModel.uiState.value
        assertEquals("", estado.credenciales.correo)
        assertEquals("", estado.credenciales.contrasena)
        assertFalse(estado.contrasenaVisible)
        assertFalse(estado.botonHabilitado)
        assertNull(estado.errorGeneral)
    }

    @Test
    fun actualizarCamposHabilitaElBoton() {
        viewModel.onCorreoCambiado("test@example.com")
        viewModel.onContrasenaCambia("123456")

        val estado = viewModel.uiState.value
        assertEquals("test@example.com", estado.credenciales.correo)
        assertEquals("123456", estado.credenciales.contrasena)
        assertTrue(estado.botonHabilitado)
    }

    @Test
    fun toggleVisibilidadContrasenaAlternaEstado() {
        assertFalse(viewModel.uiState.value.contrasenaVisible)
        viewModel.onToggleVisibilidadContrasena()
        assertTrue(viewModel.uiState.value.contrasenaVisible)
        viewModel.onToggleVisibilidadContrasena()
        assertFalse(viewModel.uiState.value.contrasenaVisible)
    }

    @Test
    fun iniciarSesionConDatosInvalidosMuestraErroresEnEstado() = runTest {
        viewModel.onCorreoCambiado("correoInvalido")
        viewModel.onContrasenaCambia("123")

        viewModel.iniciarSesion()

        val estado = viewModel.uiState.value
        assertNotNull(estado.errores.correo)
        assertNotNull(estado.errores.contrasena)
        assertNull(estado.sesionExitosa)
    }

    @Test
    fun iniciarSesionExitosoNotificaCallbackYActualizaSesion() = runTest {
        viewModel.onCorreoCambiado("invitado@example.com")
        viewModel.onContrasenaCambia("cliente123")

        var sesionObtenida: SesionUsuario? = null
        viewModel.iniciarSesion { sesion -> sesionObtenida = sesion }

        val estado = viewModel.uiState.value
        assertNotNull(estado.sesionExitosa)
        assertEquals("invitado@example.com", estado.sesionExitosa?.correo)
        assertEquals(RolUsuario.CLIENTE, estado.sesionExitosa?.rol)
        assertNotNull(sesionObtenida)
    }

    @Test
    fun iniciarSesionDemoNegocioIniciaSesionConRolNegocio() = runTest {
        var sesionObtenida: SesionUsuario? = null
        viewModel.iniciarSesionDemo(esNegocio = true) { sesion -> sesionObtenida = sesion }

        val estado = viewModel.uiState.value
        assertNotNull(estado.sesionExitosa)
        assertEquals(RolUsuario.NEGOCIO, estado.sesionExitosa?.rol)
        assertEquals(RolUsuario.NEGOCIO, sesionObtenida?.rol)
    }

    @Test
    fun errorDeAutenticacionMuestraErrorGeneral() = runTest {
        viewModel.onCorreoCambiado("usuario@noexiste.com")
        viewModel.onContrasenaCambia("passwordCorrecto")

        viewModel.iniciarSesion()

        val estado = viewModel.uiState.value
        assertNotNull(estado.errorGeneral)
        assertNull(estado.sesionExitosa)
    }
}
