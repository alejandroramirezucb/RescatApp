package com.rescatapp.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OfertaTest {
    private val packSorpresa = Oferta(
        14, "Pack Sorpresa", "Panadería La Central", Categoria.PANADERIA,
        "Surtido", 1.0, 45.0, 25.0, 5, "18:00", "20:00"
    )

    @Test
    fun calculaDescuentoYAhorroPorUnidad() {
        assertEquals(44, packSorpresa.porcentajeDescuento)
        assertEquals(20.0, packSorpresa.ahorroPorUnidad, 0.0)
    }

    @Test
    fun clasificaLaDisponibilidadSegunLasUnidades() {
        assertEquals(Disponibilidad.ALTA, conUnidades(6).disponibilidad)
        assertEquals(Disponibilidad.MEDIA, conUnidades(3).disponibilidad)
        assertEquals(Disponibilidad.BAJA, conUnidades(2).disponibilidad)
        assertEquals(Disponibilidad.AGOTADA, conUnidades(0).disponibilidad)
        assertTrue(conUnidades(0).estaAgotada)
    }

    private fun conUnidades(cantidad: Int) = packSorpresa.copy(cantidadDisponible = cantidad)

    @Test
    fun crearPedidoCopiaLosDatosDeLaOferta() {
        val pedido = packSorpresa.crearPedido(idPedido = 7)

        assertEquals(7, pedido.id)
        assertEquals(14, pedido.ofertaId)
        assertEquals(25.0, pedido.precioPagado, 0.0)
        assertEquals(20.0, pedido.ahorro, 0.0)
        assertEquals(EstadoPedido.RESERVADO, pedido.estado)
    }
}
