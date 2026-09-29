package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ConstruirProgresoPedidoUseCaseTest {

    private lateinit var useCase: ConstruirProgresoPedidoUseCase

    @Before
    fun setUp() {
        useCase = ConstruirProgresoPedidoUseCase()
    }

    @Test
    fun `cuando estado es RESERVADO el primer paso esta completado y es actual`() {
        val pasos = useCase(EstadoPedido.RESERVADO)

        assertEquals(4, pasos.size)
        assertEquals("Reservado", pasos[0].estado)
        assertTrue(pasos[0].completado)
        assertTrue(pasos[0].actual)

        assertEquals("Preparando", pasos[1].estado)
        assertFalse(pasos[1].completado)
        assertFalse(pasos[1].actual)

        assertFalse(pasos[2].completado)
        assertFalse(pasos[3].completado)
    }

    @Test
    fun `preparando completa los dos primeros pasos y marca el segundo como actual`() {
        val pasos = useCase(EstadoPedido.PREPARANDO)

        assertEquals(4, pasos.size)
        assertTrue(pasos[0].completado)
        assertFalse(pasos[0].actual)

        assertTrue(pasos[1].completado)
        assertTrue(pasos[1].actual)

        assertFalse(pasos[2].completado)
        assertFalse(pasos[3].completado)
    }

    @Test
    fun `cuando estado es LISTO tres pasos estan completados y el tercero es actual`() {
        val pasos = useCase(EstadoPedido.LISTO)

        assertEquals(4, pasos.size)
        assertTrue(pasos[0].completado)
        assertTrue(pasos[1].completado)
        assertTrue(pasos[2].completado)
        assertTrue(pasos[2].actual)
        assertFalse(pasos[3].completado)
    }

    @Test
    fun `cuando estado es RECOGIDO todos los pasos estan completados y el cuarto es actual`() {
        val pasos = useCase(EstadoPedido.RECOGIDO)

        assertEquals(4, pasos.size)
        assertTrue(pasos.all { it.completado })
        assertTrue(pasos[3].actual)
    }

    @Test
    fun `cuando estado es CANCELADO ningun paso esta completado ni actual`() {
        val pasos = useCase(EstadoPedido.CANCELADO)

        assertEquals(4, pasos.size)
        assertTrue(pasos.none { it.completado })
        assertTrue(pasos.none { it.actual })
    }

    @Test
    fun `al pasar un objeto Pedido calcula correctamente los pasos`() {
        val pedido = crearPedidoBase(EstadoPedido.PREPARANDO)
        val pasos = useCase(pedido)

        assertEquals(4, pasos.size)
        assertTrue(pasos[1].actual)
    }

    private fun crearPedidoBase(estado: EstadoPedido) = Pedido(
        id = 1,
        ofertaId = 1,
        nombreOferta = "Pan artesanal",
        comercio = "Panaderia Central",
        categoria = Categoria.PANADERIA,
        precioPagado = 10.0,
        ahorro = 5.0,
        pesoKg = 0.5,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00",
        estado = estado
    )
}
