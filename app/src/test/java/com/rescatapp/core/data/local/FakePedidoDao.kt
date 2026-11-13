package com.rescatapp.core.data.local

import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.Pedido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FakePedidoDao(pedidosIniciales: List<Pedido> = pedidosDeEjemplo) : PedidoDao {

    private val entidades = MutableStateFlow(
        pedidosIniciales.map { it.toEntity() }.sortedByDescending { it.id }
    )

    override suspend fun insertar(pedido: PedidoEntity): Long {
        val nuevoId = if (pedido.id == 0) {
            (entidades.value.maxOfOrNull { it.id } ?: 0) + 1
        } else {
            pedido.id
        }
        val entidadConId = pedido.copy(id = nuevoId)
        entidades.update { lista ->
            (listOf(entidadConId) + lista.filterNot { it.id == nuevoId })
                .sortedByDescending { it.id }
        }
        return nuevoId.toLong()
    }

    override suspend fun insertarTodos(pedidos: List<PedidoEntity>) {
        entidades.update { lista ->
            (pedidos + lista.filterNot { item -> pedidos.any { it.id == item.id } })
                .sortedByDescending { it.id }
        }
    }

    override suspend fun actualizar(pedido: PedidoEntity) {
        entidades.update { lista ->
            lista.map { if (it.id == pedido.id) pedido else it }
        }
    }

    override fun observarTodos(): Flow<List<PedidoEntity>> = entidades.asStateFlow()

    override suspend fun buscarPorId(id: Int): PedidoEntity? = entidades.value.find { it.id == id }

    override suspend fun eliminarPorId(id: Int): Int {
        val antes = entidades.value.size
        entidades.update { lista -> lista.filterNot { it.id == id } }
        return antes - entidades.value.size
    }
}
