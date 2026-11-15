package com.rescatapp.core.domain

import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.RolUsuario
import org.junit.Assert.assertEquals
import org.junit.Test

class SeleccionarPedidosPorRolTest {
    @Test
    fun clienteVeTodosLosPedidosYNegocioSoloLosSuyos() {
        assertEquals(5, seleccionarPedidosPorRol(pedidosDeEjemplo, RolUsuario.CLIENTE).size)
        assertEquals(
            listOf(5),
            seleccionarPedidosPorRol(pedidosDeEjemplo, RolUsuario.NEGOCIO).map { it.id }
        )
    }

    @Test
    fun negocioConNombrePersonalizadoFiltraPorEseComercio() {
        val pedidosConComercioPersonalizado = pedidosDeEjemplo.map {
            if (it.id == 1) it.copy(comercio = "Pastelería Don Juan") else it
        }
        val filtrados = seleccionarPedidosPorRol(
            pedidosConComercioPersonalizado,
            RolUsuario.NEGOCIO,
            nombreComercio = "Pastelería Don Juan"
        )
        assertEquals(listOf(1), filtrados.map { it.id })
    }
}
