package com.rescatapp.features.auth.ui.login

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.local.FakeUsuarioDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
import com.rescatapp.features.auth.domain.IniciarSesionUseCase
import com.rescatapp.features.auth.domain.ValidarLoginUseCase
import com.rescatapp.features.auth.domain.model.CredencialesLogin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorio = RepositorioAutenticacion(
        FakeUsuarioDao(UsuariosDeEjemplo.todos.map { it.toEntity() }),
        FakeSesionDao()
    )
    private val validarLogin = ValidarLoginUseCase()
    private val viewModel =
        LoginViewModel(IniciarSesionUseCase(repositorio, validarLogin), validarLogin)

    @Test
    fun estadoInicialEsVacioYDeshabilitado() {
        val estado = viewModel.uiState.value
        assertEquals("", estado.credenciales.correo)
        assertFalse(estado.botonHabilitado)
        assertFalse(estado.sesionIniciada)
    }

    @Test
    fun actualizarCredencialesHabilitaElBoton() {
        viewModel.actualizarCredenciales(CredencialesLogin("invitado@example.com", "cliente123"))

        assertTrue(viewModel.uiState.value.botonHabilitado)
    }

    @Test
    fun credencialesInvalidasMuestranErroresDeCampo() {
        viewModel.actualizarCredenciales(CredencialesLogin("correo-invalido", "123"))

        viewModel.iniciarSesion()

        val estado = viewModel.uiState.value
        assertNotNull(estado.errores.correo)
        assertNotNull(estado.errores.contrasena)
        assertFalse(estado.sesionIniciada)
    }

    @Test
    fun actualizarCredencialesLimpiaLosErrores() {
        viewModel.actualizarCredenciales(CredencialesLogin("correo-invalido", "123"))
        viewModel.iniciarSesion()

        viewModel.actualizarCredenciales(CredencialesLogin("invitado@example.com", "123"))

        assertNull(viewModel.uiState.value.errores.correo)
    }

    @Test
    fun credencialesCorrectasInicianSesion() {
        viewModel.actualizarCredenciales(CredencialesLogin("invitado@example.com", "cliente123"))

        viewModel.iniciarSesion()

        val estado = viewModel.uiState.value
        assertTrue(estado.sesionIniciada)
        assertFalse(estado.cargando)
        assertNull(estado.errorGeneral)
    }

    @Test
    fun contrasenaIncorrectaMuestraErrorGeneral() {
        viewModel.actualizarCredenciales(CredencialesLogin("invitado@example.com", "otraClave"))

        viewModel.iniciarSesion()

        val estado = viewModel.uiState.value
        assertFalse(estado.sesionIniciada)
        assertEquals("La contraseña ingresada es incorrecta.", estado.errorGeneral)
    }

    @Test
    fun accesoDemoNegocioIniciaSesion() {
        viewModel.iniciarSesionDemo(esNegocio = true)

        assertTrue(viewModel.uiState.value.sesionIniciada)
    }
}
