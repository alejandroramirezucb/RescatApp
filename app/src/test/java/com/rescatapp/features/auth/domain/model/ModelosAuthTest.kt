package com.rescatapp.features.auth.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelosAuthTest {

    @Test
    fun erroresLoginDetectaPresenciaDeErrores() {
        val sinErrores = ErroresLogin()
        assertFalse(sinErrores.hayErrores)

        val conErrorCorreo = ErroresLogin(correo = "Correo inválido")
        assertTrue(conErrorCorreo.hayErrores)

        val conErrorContrasena = ErroresLogin(contrasena = "Contraseña muy corta")
        assertTrue(conErrorContrasena.hayErrores)
    }

    @Test
    fun erroresRegistroDetectaPresenciaDeErrores() {
        val sinErrores = ErroresRegistro()
        assertFalse(sinErrores.hayErrores)

        val conErrorNombreNegocio = ErroresRegistro(nombreNegocio = "Nombre de negocio requerido")
        assertTrue(conErrorNombreNegocio.hayErrores)

        val conErrorConfirmacion =
            ErroresRegistro(confirmarContrasena = "Las contraseñas no coinciden")
        assertTrue(conErrorConfirmacion.hayErrores)
    }
}
