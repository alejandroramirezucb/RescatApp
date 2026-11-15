package com.rescatapp.features.auth.domain

import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.local.FakeUsuarioDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
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

    private lateinit var usuarioDao: FakeUsuarioDao
    private lateinit var sesionDao: FakeSesionDao
    private lateinit var repositorio: RepositorioAutenticacion

    private lateinit var validarLogin: ValidarLoginUseCase
    private lateinit var validarRegistro: ValidarRegistroUsuarioUseCase
    private lateinit var iniciarSesion: IniciarSesionUseCase
    private lateinit var registrarUsuario: RegistrarUsuarioUseCase
    private lateinit var cerrarSesion: CerrarSesionUseCase
    private lateinit var observarSesion: ObservarSesionUsuarioUseCase

    @Before
    fun preparar() {
        usuarioDao = FakeUsuarioDao(UsuariosDeEjemplo.todos.map { it.toEntity() })
        sesionDao = FakeSesionDao()
        repositorio = RepositorioAutenticacion(usuarioDao, sesionDao)

        validarLogin = ValidarLoginUseCase()
        validarRegistro = ValidarRegistroUsuarioUseCase()
        iniciarSesion = IniciarSesionUseCase(repositorio, validarLogin)
        registrarUsuario = RegistrarUsuarioUseCase(repositorio, validarRegistro)
        cerrarSesion = CerrarSesionUseCase(repositorio)
        observarSesion = ObservarSesionUsuarioUseCase(repositorio)
    }

    @Test
    fun iniciarSesionConDatosInvalidosFallaSinConsultarRepositorio() = runTest {
        val resultado = iniciarSesion(CredencialesLogin(correo = "mal", contrasena = "12"))
        assertTrue(resultado is ResultadoOperacion.Error)
        assertEquals("Ingresa un correo electrónico válido", (resultado as ResultadoOperacion.Error).mensaje)
    }

    @Test
    fun iniciarSesionValidoEjecutaLoginYEmiteNuevaSesion() = runTest {
        val resultado = iniciarSesion(
            CredencialesLogin(correo = "invitado@example.com", contrasena = "cliente123")
        )
        assertTrue(resultado is ResultadoOperacion.Exito)

        val sesion = observarSesion().first()
        assertEquals("invitado@example.com", sesion?.correo)
    }

    @Test
    fun registrarUsuarioInvalidoFallaEnValidacion() = runTest {
        val resultado = registrarUsuario(
            DatosRegistro(nombre = "", correo = "valido@test.com", contrasena = "123456", confirmarContrasena = "123456")
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
        assertEquals("Pedro Pascal", sesion.nombre)
        assertEquals(RolUsuario.CLIENTE, sesion.rol)
    }

    @Test
    fun cerrarSesionLimpiaLaSesionObservada() = runTest {
        iniciarSesion(CredencialesLogin(correo = "invitado@example.com", contrasena = "cliente123"))
        assertEquals("invitado@example.com", observarSesion().first()?.correo)

        cerrarSesion()
        assertNull(observarSesion().first())
    }
}
