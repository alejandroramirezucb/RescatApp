package com.rescatapp.features.carrito.domain

import com.rescatapp.core.data.RepositorioCarrito
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.crearRepositorioPedidos
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.ResultadoOperacion
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfirmarCarritoUseCaseTest {
    private val api = SupabaseApiFalsa()
    private val repositorioCarrito = RepositorioCarrito()
    private val repositorioOfertas = crearRepositorioOfertas(api = api)
    private val repositorioPedidos = crearRepositorioPedidos(api = api)
    private val confirmarCarrito =
        ConfirmarCarritoUseCase(repositorioCarrito, repositorioOfertas, repositorioPedidos)

    @Test
    fun reservaCadaUnidadDescuentaStockYVaciaElCarrito() = runTest {
        repositorioCarrito.cambiarCantidad(ofertasDeEjemplo.first { it.id == 13 }, 2)
        repositorioCarrito.agregar(ofertasDeEjemplo.first { it.id == 14 })

        val resultado = confirmarCarrito()

        assertEquals(ResultadoOperacion.Exito(3), resultado)
        assertEquals(3, repositorioPedidos.pedidos.first().size)
        assertEquals(1, repositorioOfertas.obtenerPorId(13).first()?.cantidadDisponible)
        assertEquals(4, repositorioOfertas.obtenerPorId(14).first()?.cantidadDisponible)
        assertTrue(repositorioCarrito.carrito.value.items.isEmpty())
    }

    @Test
    fun noReservaNadaSiFaltaStock() = runTest {
        repositorioCarrito.cambiarCantidad(ofertasDeEjemplo.first { it.id == 13 }, 3)
        api.ofertas.replaceAll { if (it.id == 13) it.copy(cantidadDisponible = 1) else it }

        val resultado = confirmarCarrito()

        assertEquals(
            ResultadoOperacion.Error("No hay stock suficiente de Pack Croissants (quedan 1)."),
            resultado
        )
        assertTrue(repositorioPedidos.pedidos.first().isEmpty())
        assertEquals(3, repositorioCarrito.carrito.value.cantidadDe(13))
    }
}
