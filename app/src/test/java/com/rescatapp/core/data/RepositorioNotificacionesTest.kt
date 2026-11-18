package com.rescatapp.core.data

import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import java.time.LocalDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioNotificacionesTest {
    private fun notificacion(id: String, fecha: LocalDateTime, leida: Boolean = false) =
        Notificacion(
            id = id,
            titulo = "Pedido listo",
            mensaje = "Retira tu pedido",
            fechaHora = fecha,
            leida = leida,
            tipo = TipoNotificacion.PEDIDO_LISTO,
            rolDestino = RolUsuario.CLIENTE
        )

    private val api = SupabaseApiFalsa(
        notificaciones = listOf(notificacion("antigua", LocalDateTime.now().minusDays(1)))
    )
    private val repositorio = crearRepositorioNotificaciones(api = api)

    @Test
    fun sincronizarDevuelveSoloLasNotificacionesNuevasSinLeer() = runTest {
        api.notificaciones.add(notificacion("nueva", LocalDateTime.now().plusMinutes(1)))
        api.notificaciones.add(notificacion("leida", LocalDateTime.now().plusMinutes(1), true))

        val nuevas = repositorio.sincronizar()

        assertEquals(listOf("nueva"), nuevas.map { it.id })
        assertEquals(2, repositorio.contarNoLeidas(RolUsuario.CLIENTE).first())
    }

    @Test
    fun marcarComoLeidaActualizaElConteo() = runTest {
        repositorio.marcarComoLeida("antigua")

        assertEquals(0, repositorio.contarNoLeidas(RolUsuario.CLIENTE).first())
    }

    @Test
    fun eliminarTodasVaciaLaLista() = runTest {
        repositorio.eliminarTodas()

        assertTrue(repositorio.observarPorRol(RolUsuario.CLIENTE).first().isEmpty())
    }
}
