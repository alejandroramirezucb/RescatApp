package com.rescatapp.features.auth.ui.registro

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.local.FakeUsuarioDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
import com.rescatapp.features.auth.domain.RegistrarUsuarioUseCase
import com.rescatapp.features.auth.domain.ValidarRegistroUsuarioUseCase
import com.rescatapp.features.auth.domain.model.SesionUsuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RegistroUsuarioViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private lateinit var usuarioDao: FakeUsuarioDao
    private lateinit var sesionDao: FakeSesionDao
    private lateinit var repositorio: RepositorioAutenticacion
    private lateinit var registrarUsuarioUseCase: RegistrarUsuarioUseCase
    private lateinit var validarRegistroUsuarioUseCase: ValidarRegistroUsuarioUseCase
    private lateinit var viewModel: RegistroUsuarioViewModel

    @Before
    fun preparar() {
        usuarioDao = FakeUsuarioDao(UsuariosDeEjemplo.todos.map { it.toEntity() })
        sesionDao = FakeSesionDao()
        repositorio = RepositorioAutenticacion(usuarioDao, sesionDao)
        validarRegistroUsuarioUseCase = ValidarRegistroUsuarioUseCase()
        registrarUsuarioUseCase =
            RegistrarUsuarioUseCase(repositorio, validarRegistroUsuarioUseCase)
        viewModel = RegistroUsuarioViewModel(registrarUsuarioUseCase, validarRegistroUsuarioUseCase)
    }

    @Test
    fun estadoInicialEsClienteYDeshabilitado() {
        val estado = viewModel.uiState.value
        assertEquals(RolUsuario.CLIENTE, estado.datos.rol)
        assertFalse(estado.esNegocio)
        assertFalse(estado.botonHabilitado)
        assertNull(estado.errorGeneral)
    }

    @Test
    fun cambiarRolANegocioActualizaEstadoYVerificaBotonHabilitado() {
        viewModel.onRolCambiado(RolUsuario.NEGOCIO)
        assertTrue(viewModel.uiState.value.esNegocio)

        // Rellenar campos basicos sin nombre comercial
        viewModel.onNombreCambiado("Dueño Negocio")
        viewModel.onCorreoCambiado("negocio@nuevo.com")
        viewModel.onContrasenaCambia("pass123")
        viewModel.onConfirmarContrasenaCambia("pass123")
        assertFalse(viewModel.uiState.value.botonHabilitado)

        // Ahora agregar nombre comercial
        viewModel.onNombreNegocioCambiado("Mi Tienda")
        assertTrue(viewModel.uiState.value.botonHabilitado)
    }

    @Test
    fun toggleVisibilidadAlternaBanderas() {
        assertFalse(viewModel.uiState.value.contrasenaVisible)
        assertFalse(viewModel.uiState.value.confirmarContrasenaVisible)

        viewModel.onToggleVisibilidadContrasena()
        assertTrue(viewModel.uiState.value.contrasenaVisible)

        viewModel.onToggleVisibilidadConfirmarContrasena()
        assertTrue(viewModel.uiState.value.confirmarContrasenaVisible)
    }

    @Test
    fun registroConDatosInvalidosMuestraErrores() = runTest {
        viewModel.onNombreCambiado("")
        viewModel.onCorreoCambiado("invalido")
        viewModel.onContrasenaCambia("12")
        viewModel.onConfirmarContrasenaCambia("otra")

        viewModel.registrarUsuario()

        val estado = viewModel.uiState.value
        assertNotNull(estado.errores.nombre)
        assertNotNull(estado.errores.correo)
        assertNotNull(estado.errores.contrasena)
        assertNotNull(estado.errores.confirmarContrasena)
        assertNull(estado.sesionExitosa)
    }

    @Test
    fun registroExitosoNotificaCallbackYGuardaSesion() = runTest {
        viewModel.onNombreCambiado("Fernanda Ruiz")
        viewModel.onCorreoCambiado("fernanda@example.com")
        viewModel.onContrasenaCambia("secreta123")
        viewModel.onConfirmarContrasenaCambia("secreta123")
        viewModel.onTelefonoCambiado("911223344")
        viewModel.onDireccionCambia("Calle Verde 99")

        var sesionCreada: SesionUsuario? = null
        viewModel.registrarUsuario { sesion -> sesionCreada = sesion }

        val estado = viewModel.uiState.value
        assertNotNull(estado.sesionExitosa)
        assertEquals("Fernanda Ruiz", estado.sesionExitosa?.nombre)
        assertEquals("fernanda@example.com", estado.sesionExitosa?.correo)
        assertNotNull(sesionCreada)
    }

    @Test
    fun registroConCorreoExistenteMuestraErrorGeneral() = runTest {
        viewModel.onNombreCambiado("Invitado Duplicado")
        viewModel.onCorreoCambiado("invitado@example.com")
        viewModel.onContrasenaCambia("clave123")
        viewModel.onConfirmarContrasenaCambia("clave123")

        viewModel.registrarUsuario()

        val estado = viewModel.uiState.value
        assertNotNull(estado.errorGeneral)
        assertTrue(estado.errorGeneral!!.contains("Ya existe una cuenta"))
        assertNull(estado.sesionExitosa)
    }

    @Test
    fun consumirSesionExitosaLimpiaSesionDelEstado() = runTest {
        viewModel.onNombreCambiado("Fernanda Ruiz")
        viewModel.onCorreoCambiado("fernanda@example.com")
        viewModel.onContrasenaCambia("secreta123")
        viewModel.onConfirmarContrasenaCambia("secreta123")
        viewModel.registrarUsuario()

        assertNotNull(viewModel.uiState.value.sesionExitosa)
        viewModel.consumirSesionExitosa()
        assertNull(viewModel.uiState.value.sesionExitosa)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun llamadasMultiplesDuranteCargaSonIgnoradas() = runTest(StandardTestDispatcher()) {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        viewModel.onNombreCambiado("Fernanda Ruiz")
        viewModel.onCorreoCambiado("fernanda@example.com")
        viewModel.onContrasenaCambia("secreta123")
        viewModel.onConfirmarContrasenaCambia("secreta123")

        var contadorInvocaciones = 0
        viewModel.registrarUsuario { contadorInvocaciones++ }
        assertTrue(viewModel.uiState.value.cargando)

        // Segunda llamada mientras cargando es true
        viewModel.registrarUsuario { contadorInvocaciones++ }

        testScheduler.advanceUntilIdle()
        assertEquals(1, contadorInvocaciones)
    }
}
