package com.rescatapp.core.data

import com.rescatapp.core.model.Pedido
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class RepositorioPedidos(pedidosIniciales: List<Pedido>) {
    @Inject
    constructor() : this(pedidosDeEjemplo)

    private val pedidosEditables = MutableStateFlow(pedidosIniciales)
    val pedidos: StateFlow<List<Pedido>> = pedidosEditables.asStateFlow()

    fun buscarPorId(id: Int): Pedido? = pedidos.value.find { it.id == id }

    fun agregar(pedido: Pedido): Pedido {
        val pedidoNuevo = pedido.copy(id = generarSiguienteId(pedidos.value.map { it.id }))
        pedidosEditables.update { listOf(pedidoNuevo) + it }
        return pedidoNuevo
    }

    fun actualizar(pedido: Pedido) {
        pedidosEditables.update { lista -> lista.map { if (it.id == pedido.id) pedido else it } }
    }
}
