package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CancelarPedidoUseCaseTest {
    private val repositorioOfertas = crearRepositorioOfertas()
    private val repositorioPedidos = RepositorioPedidos(FakePedidoDao())
    private val cancelarPedido = CancelarPedidoUseCase(repositorioPedidos, repositorioOfertas)

    @Test
    fun cancelarUnPedidoReservadoDevuelveLaUnidadALaOferta() = runTest {
        val packCroissants = repositorioOfertas.buscarPorId(13)!!
        repositorioOfertas.cambiarUnidadesDisponibles(packCroissants.id, diferencia = -1)
        val pedido = repositorioPedidos.agregar(packCroissants.crearPedido(idPedido = 0))

        cancelarPedido(pedido.id)

        assertEquals(EstadoPedido.CANCELADO, repositorioPedidos.buscarPorId(pedido.id)?.estado)
        assertEquals(3, repositorioOfertas.buscarPorId(13)?.cantidadDisponible)
    }

    @Test
    fun noCancelaUnPedidoQueYaSeEstaPreparando() = runTest {
        val resultado = cancelarPedido(4)

        val errorEsperado = ResultadoOperacion.Error("Solo puedes cancelar un pedido reservado")
        assertEquals(errorEsperado, resultado)
        assertEquals(EstadoPedido.PREPARANDO, repositorioPedidos.buscarPorId(4)?.estado)
    }
}
