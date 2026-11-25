package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.crearRepositorioPedidos
import com.rescatapp.core.model.EstadoPagoSimulado
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import java.io.IOException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CancelarPedidoUseCaseTest {
    private val api = SupabaseApiFalsa()
    private val repositorioOfertas = crearRepositorioOfertas(api = api)
    private val repositorioPedidos = crearRepositorioPedidos(api = api)
    private val cancelarPedido = CancelarPedidoUseCase(repositorioPedidos, repositorioOfertas)

    @Test
    fun cancelarDevuelveLaUnidadYMarcaElPedidoComoCancelado() = runTest {
        repositorioPedidos.reservar(13)
        val pedido = repositorioPedidos.pedidos.first().single()

        cancelarPedido(pedido.id)

        assertEquals(EstadoPedido.CANCELADO, repositorioPedidos.buscarPorId(pedido.id)?.estado)
        assertEquals(
            EstadoPagoSimulado.ANULADO,
            repositorioPedidos.buscarPorId(pedido.id)?.estadoPagoSimulado
        )
        assertEquals(3, repositorioOfertas.obtenerPorId(13).first()?.cantidadDisponible)
    }

    @Test
    fun reintentarLaCancelacionNoDuplicaStock() = runTest {
        repositorioPedidos.reservar(13)
        val id = repositorioPedidos.pedidos.first().single().id

        assertTrue(cancelarPedido(id) is ResultadoOperacion.Exito)
        assertTrue(cancelarPedido(id) is ResultadoOperacion.Exito)

        assertEquals(3, api.ofertas.first { it.id == 13 }.cantidadDisponible)
        assertEquals(
            EstadoPagoSimulado.ANULADO,
            repositorioPedidos.buscarPorId(id)?.estadoPagoSimulado
        )
    }

    @Test
    fun pedidosAceptadosOEntregadosSeRechazanAntesDeLlamarAlRpc() = runTest {
        repositorioPedidos.reservar(13)
        val pedido = repositorioPedidos.pedidos.first().single()
        for (estado in listOf(EstadoPedido.PREPARANDO, EstadoPedido.LISTO, EstadoPedido.RECOGIDO)) {
            api.pedidos[0] = pedido.copy(estado = estado)
            repositorioPedidos.sincronizar()

            assertTrue(cancelarPedido(pedido.id) is ResultadoOperacion.Error)
            assertEquals(estado, repositorioPedidos.buscarPorId(pedido.id)?.estado)
            assertEquals(
                EstadoPagoSimulado.APROBADO,
                repositorioPedidos.buscarPorId(pedido.id)?.estadoPagoSimulado
            )
        }
        assertEquals(0, api.cancelacionesSolicitadas)
        assertEquals(2, api.ofertas.first { it.id == 13 }.cantidadDisponible)
    }

    @Test
    fun siLaTiendaAceptoMientrasSeConfirmabaNoAnulaElPagoYActualizaLaPantalla() = runTest {
        repositorioPedidos.reservar(13)
        val pedido = repositorioPedidos.pedidos.first().single()
        api.pedidos[0] = pedido.copy(estado = EstadoPedido.PREPARANDO)

        assertTrue(cancelarPedido(pedido.id) is ResultadoOperacion.Error)

        assertEquals(EstadoPedido.PREPARANDO, repositorioPedidos.buscarPorId(pedido.id)?.estado)
        assertEquals(
            EstadoPagoSimulado.APROBADO,
            repositorioPedidos.buscarPorId(pedido.id)?.estadoPagoSimulado
        )
        assertEquals(2, api.ofertas.first { it.id == 13 }.cantidadDisponible)
    }

    @Test
    fun unFalloDeRedNoCancelaNiAnulaNiDevuelveStock() = runTest {
        repositorioPedidos.reservar(13)
        val pedido = repositorioPedidos.pedidos.first().single()
        api.fallo = IOException()

        val resultado = cancelarPedido(pedido.id)

        assertTrue((resultado as ResultadoOperacion.Error).mensaje.contains("conectarnos"))
        assertEquals(EstadoPedido.RESERVADO, repositorioPedidos.buscarPorId(pedido.id)?.estado)
        assertEquals(
            EstadoPagoSimulado.APROBADO,
            repositorioPedidos.buscarPorId(pedido.id)?.estadoPagoSimulado
        )
        assertEquals(2, api.ofertas.first { it.id == 13 }.cantidadDisponible)
    }

    @Test
    fun pedidoInexistenteNoLlamaAlServidor() = runTest {
        assertTrue(cancelarPedido(-1) is ResultadoOperacion.Error)
        assertEquals(0, api.cancelacionesSolicitadas)
    }
}
