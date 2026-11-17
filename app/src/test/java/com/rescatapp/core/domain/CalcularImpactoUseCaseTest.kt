package com.rescatapp.core.domain

import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.EstadoPedido
import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularImpactoUseCaseTest {
    private val calcularImpacto = CalcularImpactoUseCase()

    @Test
    fun soloLosPedidosRecogidosGeneranImpacto() {
        val impacto = calcularImpacto(pedidosDeEjemplo)

        assertEquals(2, impacto.rescates)
        assertEquals(47.0, impacto.ahorrado, 0.001)
        assertEquals(1.4, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun cancelarUnPedidoLoRestaDelImpacto() {
        val pedidos = pedidosDeEjemplo.map {
            if (it.id == 3) it.copy(estado = EstadoPedido.CANCELADO) else it
        }

        val impacto = calcularImpacto(pedidos)

        assertEquals(1, impacto.rescates)
        assertEquals(30.0, impacto.ahorrado, 0.001)
        assertEquals(0.9, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun sinPedidosElImpactoEsCero() {
        assertEquals(Impacto(), calcularImpacto(emptyList()))
    }
}
