package com.rescatapp.features.auth.domain

import com.rescatapp.features.auth.domain.model.CredencialesLogin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidarLoginUseCaseTest {

    private val validador = ValidarLoginUseCase()

    @Test
    fun credencialesValidasNoRetornanErrores() {
        val credenciales = CredencialesLogin(
            correo = "usuario@rescatapp.com",
            contrasena = "password123"
        )
        val resultado = validador(credenciales)
        assertFalse(resultado.hayErrores)
        assertNull(resultado.correo)
        assertNull(resultado.contrasena)
    }

    @Test
    fun correoVacioRetornaError() {
        val resultado = validador(CredencialesLogin(correo = "  ", contrasena = "123456"))
        assertTrue(resultado.hayErrores)
        assertNotNull(resultado.correo)
        assertEquals("Ingresa tu correo electrónico", resultado.correo)
    }

    @Test
    fun correoFormatoInvalidoRetornaError() {
        val resultado =
            validador(CredencialesLogin(correo = "correosinformato", contrasena = "123456"))
        assertTrue(resultado.hayErrores)
        assertEquals("Ingresa un correo electrónico válido", resultado.correo)
    }

    @Test
    fun contrasenaVaciaRetornaError() {
        val resultado = validador(CredencialesLogin(correo = "correo@valido.com", contrasena = ""))
        assertTrue(resultado.hayErrores)
        assertEquals("Ingresa tu contraseña", resultado.contrasena)
    }

    @Test
    fun contrasenaCortaRetornaError() {
        val resultado =
            validador(CredencialesLogin(correo = "correo@valido.com", contrasena = "12345"))
        assertTrue(resultado.hayErrores)
        assertEquals("La contraseña debe tener al menos 6 caracteres", resultado.contrasena)
    }
}
