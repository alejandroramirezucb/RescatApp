package com.rescatapp.core.data

import com.rescatapp.core.data.mock.pedidosDeEjemplo
import com.rescatapp.core.model.Pedido
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class RepositorioPedidos(pedidosIniciales: List<Pedido> = pedidosDeEjemplo) {
    @Inject
    constructor() : this(pedidosDeEjemplo)

    private val pedidosMutables = MutableStateFlow(pedidosIniciales)
    val pedidos = pedidosMutables.asStateFlow()

    fun obtenerActualPorId(id: Int): Pedido? = pedidos.value.find { it.id == id }

    fun agregar(pedido: Pedido): Pedido {
        val nuevoId = (pedidos.value.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoPedido = pedido.copy(id = nuevoId)
        pedidosMutables.update { listOf(nuevoPedido) + it }
        return nuevoPedido
    }

    fun actualizar(pedido: Pedido) {
        pedidosMutables.update { lista ->
            lista.map { actual -> if (actual.id == pedido.id) pedido else actual }
        }
    }
}
