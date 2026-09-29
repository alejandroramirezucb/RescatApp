package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import org.junit.Assert.assertEquals
import org.junit.Test

class CancelarPedidoUseCaseTest {
    private val repositorioOfertas = RepositorioOfertas()
    private val repositorioPedidos = RepositorioPedidos()
    private val cancelarPedido = CancelarPedidoUseCase(repositorioPedidos, repositorioOfertas)

    @Test
    fun cancelarUnPedidoReservadoDevuelveLaUnidadALaOferta() {
        val packCroissants = repositorioOfertas.buscarPorId(13)!!
        repositorioOfertas.actualizar(packCroissants.copy(cantidadDisponible = 2))
        val pedido = repositorioPedidos.agregar(packCroissants.crearPedido(idPedido = 0))

        cancelarPedido(pedido.id)

        assertEquals(EstadoPedido.CANCELADO, repositorioPedidos.buscarPorId(pedido.id)?.estado)
        assertEquals(3, repositorioOfertas.buscarPorId(13)?.cantidadDisponible)
    }

    @Test
    fun noCancelaUnPedidoQueYaSeEstaPreparando() {
        val resultado = cancelarPedido(4)

        val errorEsperado = ResultadoOperacion.Error("Solo puedes cancelar un pedido reservado")
        assertEquals(errorEsperado, resultado)
        assertEquals(EstadoPedido.PREPARANDO, repositorioPedidos.buscarPorId(4)?.estado)
    }
}
