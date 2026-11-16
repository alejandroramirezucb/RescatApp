package com.rescatapp.core.data

import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.local.FakeUsuarioDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.SesionUsuario
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositorioAutenticacionRegistroTest {

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
    fun registrarUsuarioClienteExitosoGuardaYAutentica() = runTest {
        val datos = DatosRegistro(
            nombre = "Laura Mendoza",
            correo = "laura@example.com",
            contrasena = "laura123",
            confirmarContrasena = "laura123",
            rol = RolUsuario.CLIENTE,
            telefono = "99887766",
            direccion = "Calle 10 #20"
        )

        val resultado = repositorio.registrarUsuario(datos)

        assertTrue(resultado is ResultadoOperacion.Exito)
        val sesion = (resultado as ResultadoOperacion.Exito).valor as SesionUsuario
        assertEquals("Laura Mendoza", sesion.nombre)
        assertEquals("laura@example.com", sesion.correo)
        assertEquals(RolUsuario.CLIENTE, sesion.rol)

        val enDb = usuarioDao.buscarPorCorreo("laura@example.com")
        assertNotNull(enDb)
        assertEquals("99887766", enDb?.telefono)
    }

    @Test
    fun registrarUsuarioNegocioGuardaNombreComercial() = runTest {
        val datos = DatosRegistro(
            nombre = "Carlos Dueño",
            correo = "carlos@bistro.com",
            contrasena = "bistro123",
            confirmarContrasena = "bistro123",
            rol = RolUsuario.NEGOCIO,
            nombreNegocio = "Bistró Francés"
        )

        val resultado = repositorio.registrarUsuario(datos)

        assertTrue(resultado is ResultadoOperacion.Exito)
        val sesion = (resultado as ResultadoOperacion.Exito).valor as SesionUsuario
        assertEquals(RolUsuario.NEGOCIO, sesion.rol)
        assertEquals("Bistró Francés", sesion.nombreNegocio)
        assertEquals(RolUsuario.NEGOCIO, UsuarioDemo.rol.value)
    }

    @Test
    fun registrarUsuarioConCorreoDuplicadoRetornaError() = runTest {
        val datos = DatosRegistro(
            nombre = "Intento Duplicado",
            correo = "invitado@example.com",
            contrasena = "pass123",
            confirmarContrasena = "pass123"
        )

        val resultado = repositorio.registrarUsuario(datos)

        assertTrue(resultado is ResultadoOperacion.Error)
        val mensaje = (resultado as ResultadoOperacion.Error).mensaje
        assertTrue(mensaje.contains("Ya existe una cuenta"))
    }

    @Test
    fun registrarUsuarioNormalizaCorreoEnMinusculasYDetectaDuplicados() = runTest {
        val datos = DatosRegistro(
            nombre = "Esteban",
            correo = "  ESTEBAN@EMPRESA.COM  ",
            contrasena = "pass123",
            confirmarContrasena = "pass123"
        )
        val resultado = repositorio.registrarUsuario(datos)
        assertTrue(resultado is ResultadoOperacion.Exito)
        val guardado = usuarioDao.buscarPorCorreo("esteban@empresa.com")
        assertNotNull(guardado)
        assertEquals("esteban@empresa.com", guardado?.correo)

        val intentoDuplicado = datos.copy(correo = "Esteban@Empresa.Com")
        val resultadoDup = repositorio.registrarUsuario(intentoDuplicado)
        assertTrue(resultadoDup is ResultadoOperacion.Error)
    }
}
