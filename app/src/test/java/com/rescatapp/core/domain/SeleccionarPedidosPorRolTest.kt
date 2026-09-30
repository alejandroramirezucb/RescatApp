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
}
