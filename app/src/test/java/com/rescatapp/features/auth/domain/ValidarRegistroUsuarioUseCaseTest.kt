package com.rescatapp.features.auth.domain

import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.DatosRegistro
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidarRegistroUsuarioUseCaseTest {

    private val validador = ValidarRegistroUsuarioUseCase()

    @Test
    fun datosClienteValidosNoRetornanErrores() {
        val datos = DatosRegistro(
            nombre = "Ana Maria",
            correo = "anamaria@example.com",
            contrasena = "clave123",
            confirmarContrasena = "clave123",
            rol = RolUsuario.CLIENTE
        )
        val resultado = validador(datos)
        assertFalse(resultado.hayErrores)
    }

    @Test
    fun datosNegocioValidosConNombreComercialNoRetornanErrores() {
        val datos = DatosRegistro(
            nombre = "Carlos Perez",
            correo = "carlos@panaderia.com",
            contrasena = "pan12345",
            confirmarContrasena = "pan12345",
            rol = RolUsuario.NEGOCIO,
            nombreNegocio = "Panadería Artesanal"
        )
        val resultado = validador(datos)
        assertFalse(resultado.hayErrores)
    }

    @Test
    fun negocioSinNombreComercialRetornaErrorEnNombreNegocio() {
        val datos = DatosRegistro(
            nombre = "Carlos Perez",
            correo = "carlos@panaderia.com",
            contrasena = "pan12345",
            confirmarContrasena = "pan12345",
            rol = RolUsuario.NEGOCIO,
            nombreNegocio = ""
        )
        val resultado = validador(datos)
        assertTrue(resultado.hayErrores)
        assertNotNull(resultado.nombreNegocio)
        assertEquals("Ingresa el nombre de tu comercio o negocio", resultado.nombreNegocio)
    }

    @Test
    fun clienteSinNombreComercialNoRetornaErrorDeNegocio() {
        val datos = DatosRegistro(
            nombre = "Usuario Cliente",
            correo = "cliente@example.com",
            contrasena = "pass123",
            confirmarContrasena = "pass123",
            rol = RolUsuario.CLIENTE,
            nombreNegocio = ""
        )
        val resultado = validador(datos)
        assertFalse(resultado.hayErrores)
        assertNull(resultado.nombreNegocio)
    }

    @Test
    fun contrasenasNoCoincidentesRetornaError() {
        val datos = DatosRegistro(
            nombre = "Juan Perez",
            correo = "juan@example.com",
            contrasena = "pass123",
            confirmarContrasena = "otraCosa123"
        )
        val resultado = validador(datos)
        assertTrue(resultado.hayErrores)
        assertEquals("Las contraseñas no coinciden", resultado.confirmarContrasena)
    }

    @Test
    fun nombreVacioOCortoRetornaError() {
        val datos = DatosRegistro(
            nombre = "A",
            correo = "juan@example.com",
            contrasena = "pass123",
            confirmarContrasena = "pass123"
        )
        val resultado = validador(datos)
        assertTrue(resultado.hayErrores)
        assertEquals("El nombre debe tener al menos 2 caracteres", resultado.nombre)
    }
}
