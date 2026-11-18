package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.crearRepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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
        assertEquals(3, repositorioOfertas.obtenerPorId(13).first()?.cantidadDisponible)
    }
}
