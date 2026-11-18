package com.rescatapp.features.auth.ui.registro

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearAutenticacion
import com.rescatapp.core.data.errorServidor
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.RegistrarUsuarioUseCase
import com.rescatapp.features.auth.domain.ValidarRegistroUsuarioUseCase
import com.rescatapp.features.auth.domain.model.DatosRegistro
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegistroUsuarioViewModelTest {

    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val api = SupabaseApiFalsa()
    private val repositorio = crearAutenticacion(api, sesion = null)
    private val validarRegistro = ValidarRegistroUsuarioUseCase()
    private val viewModel = RegistroUsuarioViewModel(
        RegistrarUsuarioUseCase(repositorio, validarRegistro),
        validarRegistro
    )
    private val datosValidos = DatosRegistro(
        nombre = "Fernanda Ruiz",
        correo = "fernanda@example.com",
        contrasena = "secreta123",
        confirmarContrasena = "secreta123"
    )

    @Test
    fun estadoInicialEsClienteYDeshabilitado() {
        val estado = viewModel.uiState.value
        assertEquals(RolUsuario.CLIENTE, estado.datos.rol)
        assertFalse(estado.esNegocio)
        assertFalse(estado.botonHabilitado)
    }

    @Test
    fun negocioSinNombreComercialMantieneElBotonDeshabilitado() {
        viewModel.actualizarDatos(datosValidos.copy(rol = RolUsuario.NEGOCIO))
        assertFalse(viewModel.uiState.value.botonHabilitado)

        viewModel.actualizarDatos(
            datosValidos.copy(rol = RolUsuario.NEGOCIO, nombreNegocio = "Mi Tienda")
        )
        assertTrue(viewModel.uiState.value.botonHabilitado)
    }

    @Test
    fun registroConDatosInvalidosMuestraErrores() {
        viewModel.actualizarDatos(DatosRegistro(correo = "invalido", contrasena = "12"))

        viewModel.registrarUsuario()

        val estado = viewModel.uiState.value
        assertNotNull(estado.errores.nombre)
        assertNotNull(estado.errores.correo)
        assertNotNull(estado.errores.contrasena)
        assertNotNull(estado.errores.confirmarContrasena)
        assertFalse(estado.sesionIniciada)
    }

    @Test
    fun registroExitosoIniciaSesion() {
        viewModel.actualizarDatos(datosValidos)

        viewModel.registrarUsuario()

        val estado = viewModel.uiState.value
        assertTrue(estado.sesionIniciada)
        assertNull(estado.errorGeneral)
    }

    @Test
    fun registroConCorreoExistenteMuestraErrorGeneral() {
        api.fallo = errorServidor("User already registered")
        viewModel.actualizarDatos(datosValidos.copy(correo = "invitado@example.com"))

        viewModel.registrarUsuario()

        val estado = viewModel.uiState.value
        assertTrue(estado.errorGeneral.orEmpty().contains("Ya existe una cuenta"))
        assertFalse(estado.sesionIniciada)
    }
}
