package com.rescatapp.core.domain

import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.EstadoPedido
import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularImpactoUseCaseTest {
    private val calcularImpacto = CalcularImpactoUseCase()

    @Test
    fun conLosPedidosDeEjemploIgnoraElCancelado() {
        val impacto = calcularImpacto(pedidosDeEjemplo)

        assertEquals(4, impacto.rescates)
        assertEquals(102.0, impacto.ahorrado, 0.001)
        assertEquals(3.6, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun cancelarUnPedidoLoRestaDelImpacto() {
        val pedidos = pedidosDeEjemplo.map {
            if (it.id == 5) it.copy(estado = EstadoPedido.CANCELADO) else it
        }

        val impacto = calcularImpacto(pedidos)

        assertEquals(3, impacto.rescates)
        assertEquals(82.0, impacto.ahorrado, 0.001)
        assertEquals(2.6, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun sinPedidosElImpactoEsCero() {
        assertEquals(Impacto(), calcularImpacto(emptyList()))
    }
}
