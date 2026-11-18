package com.rescatapp.features.auth.ui.login

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearAutenticacion
import com.rescatapp.core.data.errorServidor
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

    private val api = SupabaseApiFalsa()
    private val repositorio = crearAutenticacion(api, sesion = null)
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
        api.fallo = errorServidor("Invalid login credentials")
        viewModel.actualizarCredenciales(CredencialesLogin("invitado@example.com", "otraClave"))

        viewModel.iniciarSesion()

        val estado = viewModel.uiState.value
        assertFalse(estado.sesionIniciada)
        assertEquals("Correo o contraseña incorrectos.", estado.errorGeneral)
    }
}
