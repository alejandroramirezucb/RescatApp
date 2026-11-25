package com.rescatapp.core.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositorioCarritoTest {
    private val repositorio = RepositorioCarrito()
    private val packSorpresa = ofertasDeEjemplo.first { it.id == 14 }
    private val packCroissants = ofertasDeEjemplo.first { it.id == 13 }

    @Test
    fun agregarDosOfertasCalculaTotalAhorroYUnidades() {
        repositorio.agregar(packSorpresa)
        repositorio.agregar(packSorpresa)
        repositorio.agregar(packCroissants)

        val carrito = repositorio.carrito.value
        assertEquals(3, carrito.unidades)
        assertEquals(65.0, carrito.total, 0.001)
        assertEquals(55.0, carrito.ahorro, 0.001)
    }

    @Test
    fun noSuperaLaCantidadDisponible() {
        repeat(5) { repositorio.agregar(packCroissants) }

        assertEquals(3, repositorio.carrito.value.cantidadDe(13))
    }

    @Test
    fun cambiarCantidadACeroQuitaElProducto() {
        repositorio.agregar(packCroissants)

        repositorio.cambiarCantidad(packCroissants, 0)

        assertTrue(repositorio.carrito.value.items.isEmpty())
    }

    @Test
    fun eliminarQuitaSoloEseProducto() {
        repositorio.agregar(packSorpresa)
        repositorio.agregar(packCroissants)

        repositorio.eliminar(14)

        assertEquals(listOf(13), repositorio.carrito.value.items.map { it.oferta.id })
    }
}
