package com.rescatapp.features.auth.domain.model

import com.rescatapp.core.model.RolUsuario
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelosAuthTest {

    @Test
    fun sesionUsuarioInicialCalculadaCorrectamenteParaCliente() {
        val sesion = SesionUsuario(
            usuarioId = "1",
            nombre = "Carlos Perez",
            correo = "carlos@example.com",
            rol = RolUsuario.CLIENTE
        )

        assertFalse(sesion.esNegocio)
        assertEquals("C", sesion.inicial)
    }

    @Test
    fun sesionUsuarioInicialCalculadaCorrectamenteParaNegocioConNombreComercial() {
        val sesion = SesionUsuario(
            usuarioId = "2",
            nombre = "Juan Gomez",
            correo = "panaderia@example.com",
            rol = RolUsuario.NEGOCIO,
            nombreNegocio = "Panadería Moderna"
        )

        assertTrue(sesion.esNegocio)
        assertEquals("P", sesion.inicial)
    }

    @Test
    fun erroresLoginDetectaPresenciaDeErrores() {
        val sinErrores = ErroresLogin()
        assertFalse(sinErrores.hayErrores)

        val conErrorCorreo = ErroresLogin(correo = "Correo inválido")
        assertTrue(conErrorCorreo.hayErrores)

        val conErrorContrasena = ErroresLogin(contrasena = "Contraseña muy corta")
        assertTrue(conErrorContrasena.hayErrores)

        val conErrorGeneral = ErroresLogin(general = "Error en el servidor")
        assertTrue(conErrorGeneral.hayErrores)
    }

    @Test
    fun erroresRegistroDetectaPresenciaDeErrores() {
        val sinErrores = ErroresRegistro()
        assertFalse(sinErrores.hayErrores)

        val conErrorNombreNegocio = ErroresRegistro(nombreNegocio = "Nombre de negocio requerido")
        assertTrue(conErrorNombreNegocio.hayErrores)

        val conErrorConfirmacion = ErroresRegistro(confirmarContrasena = "Las contraseñas no coinciden")
        assertTrue(conErrorConfirmacion.hayErrores)
    }
}
