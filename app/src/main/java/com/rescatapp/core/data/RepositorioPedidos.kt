package com.rescatapp.core.data

import com.rescatapp.core.model.Pedido
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class RepositorioPedidos @Inject constructor() {
    private val pedidosMutables = MutableStateFlow<List<Pedido>>(emptyList())
    val pedidos = pedidosMutables.asStateFlow()

    fun agregar(pedido: Pedido): Pedido {
        val nuevoId = (pedidos.value.maxOfOrNull { it.id } ?: 0) + 1
        val nuevoPedido = pedido.copy(id = nuevoId)
        pedidosMutables.update { listOf(nuevoPedido) + it }
        return nuevoPedido
    }
}
