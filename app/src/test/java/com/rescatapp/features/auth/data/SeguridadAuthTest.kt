package com.rescatapp.features.auth.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SeguridadAuthTest {

    @Test
    fun hashearContrasenaProduceHashConsistente() {
        val hash1 = SeguridadAuth.hashearContrasena("miContrasena123")
        val hash2 = SeguridadAuth.hashearContrasena("miContrasena123")
        assertEquals(hash1, hash2)
    }

    @Test
    fun hashearContrasenasDistintasProduceHashesDiferentes() {
        val hash1 = SeguridadAuth.hashearContrasena("clave1")
        val hash2 = SeguridadAuth.hashearContrasena("clave2")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun verificarContrasenaCorrectaRetornaTrue() {
        val pass = "SuperPassword2026!"
        val hash = SeguridadAuth.hashearContrasena(pass)
        assertTrue(SeguridadAuth.verificarContrasena(pass, hash))
    }

    @Test
    fun verificarContrasenaErroneaRetornaFalse() {
        val hash = SeguridadAuth.hashearContrasena("passwordCorrecto")
        assertFalse(SeguridadAuth.verificarContrasena("passwordIncorrecto", hash))
    }
}
