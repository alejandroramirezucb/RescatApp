package com.rescatapp.features.auth.domain

import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearAutenticacion
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.CredencialesLogin
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.SesionUsuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CasosUsoAuthTest {

    private lateinit var repositorio: RepositorioAutenticacion

    private lateinit var validarLogin: ValidarLoginUseCase
    private lateinit var validarRegistro: ValidarRegistroUsuarioUseCase
    private lateinit var iniciarSesion: IniciarSesionUseCase
    private lateinit var registrarUsuario: RegistrarUsuarioUseCase

    @Before
    fun preparar() {
        repositorio = crearAutenticacion(SupabaseApiFalsa(), sesion = null)

        validarLogin = ValidarLoginUseCase()
        validarRegistro = ValidarRegistroUsuarioUseCase()
        iniciarSesion = IniciarSesionUseCase(repositorio, validarLogin)
        registrarUsuario = RegistrarUsuarioUseCase(repositorio, validarRegistro)
    }

    @Test
    fun iniciarSesionConDatosInvalidosFallaSinConsultarRepositorio() = runTest {
        val resultado = iniciarSesion(CredencialesLogin(correo = "mal", contrasena = "12"))
        assertTrue(resultado is ResultadoOperacion.Error)
        assertEquals(
            "Ingresa un correo electrónico válido",
            (resultado as ResultadoOperacion.Error).mensaje
        )
    }

    @Test
    fun iniciarSesionValidoEjecutaLoginYEmiteNuevaSesion() = runTest {
        val resultado = iniciarSesion(
            CredencialesLogin(correo = "invitado@example.com", contrasena = "cliente123")
        )
        assertTrue(resultado is ResultadoOperacion.Exito)

        val sesion = repositorio.sesionActual.first()
        assertEquals("ana@correo.com", sesion?.correo)
    }

    @Test
    fun registrarUsuarioInvalidoFallaEnValidacion() = runTest {
        val resultado = registrarUsuario(
            DatosRegistro(
                nombre = "",
                correo = "valido@test.com",
                contrasena = "123456",
                confirmarContrasena = "123456"
            )
        )
        assertTrue(resultado is ResultadoOperacion.Error)
        assertEquals("Ingresa tu nombre completo", (resultado as ResultadoOperacion.Error).mensaje)
    }

    @Test
    fun registrarUsuarioValidoGuardaYEstableceSesion() = runTest {
        val resultado = registrarUsuario(
            DatosRegistro(
                nombre = "Pedro Pascal",
                correo = "pedro@hollywood.com",
                contrasena = "actor123",
                confirmarContrasena = "actor123",
                rol = RolUsuario.CLIENTE
            )
        )
        assertTrue(resultado is ResultadoOperacion.Exito)
        val sesion = (resultado as ResultadoOperacion.Exito).valor as SesionUsuario
        assertEquals("Ana Pérez", sesion.nombre)
        assertEquals(RolUsuario.CLIENTE, sesion.rol)
    }

    @Test
    fun cerrarSesionLimpiaLaSesionObservada() = runTest {
        iniciarSesion(CredencialesLogin(correo = "invitado@example.com", contrasena = "cliente123"))
        assertEquals("ana@correo.com", repositorio.sesionActual.first()?.correo)

        repositorio.cerrarSesion()
        assertNull(repositorio.sesionActual.first())
    }
}
