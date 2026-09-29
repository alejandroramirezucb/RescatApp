package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.PasoProgreso
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConstruirProgresoPedidoUseCaseTest {
    private val construirProgreso = ConstruirProgresoPedidoUseCase()

    @Test
    fun marcaLosPasosCompletadosYElActual() {
        val pasos = construirProgreso(EstadoPedido.PREPARANDO)

        assertEquals(
            listOf(
                PasoProgreso("Reservado", completado = true, actual = false),
                PasoProgreso("Preparando", completado = true, actual = true),
                PasoProgreso("Listo", completado = false, actual = false),
                PasoProgreso("Recogido", completado = false, actual = false)
            ),
            pasos
        )
    }

    @Test
    fun unPedidoCanceladoNoTienePasosCompletados() {
        assertTrue(construirProgreso(EstadoPedido.CANCELADO).none { it.completado })
    }
}
