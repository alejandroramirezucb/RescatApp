package com.rescatapp.core.data

import com.rescatapp.core.data.remote.PerfilDto
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.SesionUsuario
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class RepositorioAutenticacionTest {
    private val api = SupabaseApiFalsa()
    private val repositorio = crearAutenticacion(api, sesion = null)

    @Test
    fun iniciarSesionGuardaLaSesionYElRolDelPerfil() = runTest {
        api.perfil = PerfilDto("Panadería", "NEGOCIO")

        val resultado = repositorio.iniciarSesion("ANA@correo.com ", "secreta123")

        val sesion = (resultado as ResultadoOperacion.Exito).valor as SesionUsuario
        assertEquals(RolUsuario.NEGOCIO, sesion.rol)
        assertEquals("Panadería La Central", sesion.nombreNegocio)
        assertEquals(sesion, repositorio.sesionActual.first())
        assertEquals(RolUsuario.NEGOCIO, repositorio.rol.first())
    }

    @Test
    fun credencialesInvalidasMuestranUnMensajeEnEspanol() = runTest {
        api.fallo = errorServidor("Invalid login credentials")

        val resultado = repositorio.iniciarSesion("ana@correo.com", "mala")

        assertEquals(
            "Correo o contraseña incorrectos.",
            (resultado as ResultadoOperacion.Error).mensaje
        )
    }

    @Test
    fun registrarConConfirmacionPendientePideRevisarElCorreo() = runTest {
        api.confirmarCorreo = true

        val resultado = repositorio.registrarUsuario(DatosRegistro(correo = "ana@correo.com"))

        assertEquals(
            RepositorioAutenticacion.CONFIRMAR_CORREO,
            (resultado as ResultadoOperacion.Exito).valor
        )
        assertNull(repositorio.sesionActual.first())
    }

    @Test
    fun cerrarSesionBorraLaSesionYVuelveAlRolCliente() = runTest {
        api.perfil = PerfilDto("Panadería", "NEGOCIO")
        repositorio.iniciarSesion("ana@correo.com", "secreta123")

        repositorio.cerrarSesion()

        assertNull(repositorio.sesionActual.first())
        assertEquals(RolUsuario.CLIENTE, repositorio.rol.first())
    }

    @Test
    fun tokenRenuevaLaSesionCuandoVencio() = runTest {
        val vencida = crearAutenticacion(api, sesionDePrueba().copy(token = "viejo", expira = 0))

        assertEquals("Bearer acceso", vencida.token())
    }
}
