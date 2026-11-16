package com.rescatapp.core.data

import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.local.FakeUsuarioDao
import com.rescatapp.core.data.local.SesionEntity
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
import com.rescatapp.features.auth.domain.model.SesionUsuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositorioAutenticacionTest {

    private lateinit var usuarioDao: FakeUsuarioDao
    private lateinit var sesionDao: FakeSesionDao
    private lateinit var repositorio: RepositorioAutenticacion

    @Before
    fun preparar() {
        usuarioDao = FakeUsuarioDao(UsuariosDeEjemplo.todos.map { it.toEntity() })
        sesionDao = FakeSesionDao()
        repositorio = RepositorioAutenticacion(usuarioDao, sesionDao)
        UsuarioDemo.cambiarRol(RolUsuario.CLIENTE)
    }

    @Test
    fun loginConCredencialesCorrectasRetornaExitoYActualizaSesion() = runTest {
        val resultado = repositorio.iniciarSesion("invitado@example.com", "cliente123")

        assertTrue(resultado is ResultadoOperacion.Exito)
        val sesion = (resultado as ResultadoOperacion.Exito).valor as SesionUsuario
        assertEquals("invitado@example.com", sesion.correo)
        assertEquals(RolUsuario.CLIENTE, sesion.rol)

        val sesionPersistida = repositorio.sesionActual.first()
        assertNotNull(sesionPersistida)
        assertEquals("invitado@example.com", sesionPersistida?.correo)
        assertEquals(RolUsuario.CLIENTE, UsuarioDemo.rol.value)
    }

    @Test
    fun loginConNegocioActualizaRolANegocio() = runTest {
        val resultado = repositorio.iniciarSesion("contacto@lacentral.com", "negocio123")

        assertTrue(resultado is ResultadoOperacion.Exito)
        val sesion = (resultado as ResultadoOperacion.Exito).valor as SesionUsuario
        assertEquals(RolUsuario.NEGOCIO, sesion.rol)
        assertEquals(RolUsuario.NEGOCIO, UsuarioDemo.rol.value)
    }

    @Test
    fun loginConCorreoNoRegistradoRetornaError() = runTest {
        val resultado = repositorio.iniciarSesion("noexiste@example.com", "cualquiera")

        assertTrue(resultado is ResultadoOperacion.Error)
        val mensaje = (resultado as ResultadoOperacion.Error).mensaje
        assertTrue(mensaje.contains("No existe una cuenta"))
    }

    @Test
    fun loginConContrasenaIncorrectaRetornaError() = runTest {
        val resultado = repositorio.iniciarSesion("invitado@example.com", "claveErronea")

        assertTrue(resultado is ResultadoOperacion.Error)
        val mensaje = (resultado as ResultadoOperacion.Error).mensaje
        assertTrue(mensaje.contains("contraseña"))
    }

    @Test
    fun cerrarSesionLimpiaSesionActivaYRestableceRol() = runTest {
        repositorio.iniciarSesion("contacto@lacentral.com", "negocio123")
        assertEquals(RolUsuario.NEGOCIO, UsuarioDemo.rol.value)

        repositorio.cerrarSesion()

        val sesion = repositorio.sesionActual.first()
        assertNull(sesion)
        assertEquals(RolUsuario.CLIENTE, UsuarioDemo.rol.value)
        assertNull(repositorio.sesionActual.first())
    }

    @Test
    fun iniciarSesionDemoFuncionaParaClienteYNegocio() = runTest {
        val resCliente = repositorio.iniciarSesionDemo(esNegocio = false)
        assertTrue(resCliente is ResultadoOperacion.Exito)
        assertEquals(
            RolUsuario.CLIENTE,
            ((resCliente as ResultadoOperacion.Exito).valor as SesionUsuario).rol
        )

        val resNegocio = repositorio.iniciarSesionDemo(esNegocio = true)
        assertTrue(resNegocio is ResultadoOperacion.Exito)
        assertEquals(
            RolUsuario.NEGOCIO,
            ((resNegocio as ResultadoOperacion.Exito).valor as SesionUsuario).rol
        )
    }

    @Test
    fun loginConCorreoEnMayusculasOEspaciosIniciaSesionCorrectamente() = runTest {
        val resultado = repositorio.iniciarSesion("  INVITADO@EXAMPLE.COM  ", "cliente123")
        assertTrue(resultado is ResultadoOperacion.Exito)
        val sesion = (resultado as ResultadoOperacion.Exito).valor as SesionUsuario
        assertEquals("invitado@example.com", sesion.correo)
    }

    @Test
    fun sincronizarSesionRestauraRolEnInicioDeApp() = runTest {
        UsuarioDemo.cambiarRol(RolUsuario.CLIENTE)
        sesionDao.guardarSesion(
            SesionEntity(
                usuarioId = "neg-1",
                correo = "negocio@test.com",
                nombre = "Negocio Test",
                rol = RolUsuario.NEGOCIO,
                nombreNegocio = "Negocio Test"
            )
        )

        repositorio.sincronizarSesion()

        assertEquals(RolUsuario.NEGOCIO, UsuarioDemo.rol.value)
    }
}
