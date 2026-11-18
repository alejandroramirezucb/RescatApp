package com.rescatapp.core.data.local

import com.rescatapp.core.model.Pedido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakePedidoDao(pedidosIniciales: List<Pedido> = emptyList()) : PedidoDao {

    private val entidades = MutableStateFlow(pedidosIniciales.map { it.toEntity() })

    override suspend fun insertarTodos(pedidos: List<PedidoEntity>) {
        entidades.update { lista ->
            pedidos +
                lista.filterNot { item -> pedidos.any { it.id == item.id } }
        }
    }

    override suspend fun eliminarExcepto(ids: List<Int>) {
        entidades.update { lista -> lista.filter { it.id in ids } }
    }

    override fun observarTodos(): Flow<List<PedidoEntity>> =
        entidades.map { lista -> lista.sortedByDescending { it.id } }

    override suspend fun buscarPorId(id: Int): PedidoEntity? = entidades.value.find { it.id == id }
}
