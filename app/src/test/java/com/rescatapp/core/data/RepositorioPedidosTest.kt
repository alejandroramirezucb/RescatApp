package com.rescatapp.core.data

import com.rescatapp.core.model.EstadoPagoSimulado
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioPedidosTest {
    private val api = SupabaseApiFalsa()
    private val repositorio = crearRepositorioPedidos(api = api)

    @Test
    fun cancelacionConfirmadaSeGuardaAunqueFalleLaConsultaPosterior() = runTest {
        repositorio.reservar(13)
        val id = repositorio.pedidos.first().single().id
        api.falloSincronizarPedidos = IOException()

        val resultado = repositorio.cancelar(id)

        assertTrue(resultado is ResultadoOperacion.Exito)
        assertEquals(EstadoPedido.CANCELADO, repositorio.buscarPorId(id)?.estado)
        assertEquals(EstadoPagoSimulado.ANULADO, repositorio.buscarPorId(id)?.estadoPagoSimulado)
    }

    @Test(expected = CancellationException::class)
    fun cancelacionDeLaCorrutinaNoSeConvierteEnErrorDeRed() = runTest {
        api.fallo = CancellationException()
        repositorio.cancelar(1)
    }

    @Test
    fun sincronizarConservaElNombreYTelefonoDelComprador() = runTest {
        val remoto = pedidosDeEjemplo.first().copy(
            clienteNombre = "Ana Pérez",
            clienteTelefono = "71234567"
        )
        api.pedidos.add(remoto)

        repositorio.sincronizar()

        val guardado = checkNotNull(repositorio.buscarPorId(remoto.id))
        assertEquals("Ana Pérez", guardado.clienteNombre)
        assertEquals("71234567", guardado.clienteTelefono)
        assertEquals(listOf(guardado), repositorio.pedidos.first())
    }

    @Test
    fun reservarCreaElPedidoYLoGuardaEnLaBaseLocal() = runTest {
        val resultado = repositorio.reservar(13)

        val pedido = (resultado as ResultadoOperacion.Exito).valor as Pedido
        assertEquals(EstadoPedido.RESERVADO, pedido.estado)
        assertEquals(listOf(pedido.id), repositorio.pedidos.first().map { it.id })
        assertEquals(2, api.ofertas.first { it.id == 13 }.cantidadDisponible)
    }

    @Test
    fun cancelarUnPedidoQueNoEstaReservadoMuestraElMensajeDelServidor() = runTest {
        val pedido = api.reservarOferta("", mapOf("p_oferta_id" to 13)).single()
        api.avanzarPedido("", mapOf("p_pedido_id" to pedido.id))

        val resultado = repositorio.cancelar(pedido.id)

        assertEquals(
            "Solo puedes cancelar un pedido reservado",
            (resultado as ResultadoOperacion.Error).mensaje
        )
    }

    @Test
    fun avanzarPasaAlSiguienteEstado() = runTest {
        repositorio.reservar(13)
        val pedido = repositorio.pedidos.first().single()

        repositorio.avanzar(pedido, codigo = null)

        assertEquals(EstadoPedido.PREPARANDO, repositorio.buscarPorId(pedido.id)?.estado)
    }

    @Test
    fun sinSesionNoHayPedidos() = runTest {
        val sinSesion = crearRepositorioPedidos(
            pedidos = pedidosDeEjemplo,
            autenticacion = crearAutenticacion(sesion = null)
        )

        sinSesion.sincronizar()

        assertTrue(sinSesion.pedidos.first().isEmpty())
    }

    @Test
    fun unFalloDeConexionDevuelveUnMensajeComprensible() = runTest {
        api.fallo = IOException()

        val resultado = repositorio.reservar(13)

        assertTrue((resultado as ResultadoOperacion.Error).mensaje.contains("conectarnos"))
    }
}
