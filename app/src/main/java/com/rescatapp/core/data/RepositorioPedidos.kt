package com.rescatapp.core.data

import com.rescatapp.core.data.local.PedidoDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.Pedido
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RepositorioPedidos @Inject constructor(private val pedidoDao: PedidoDao) {
    val pedidos: Flow<List<Pedido>> = pedidoDao.observarTodos().map { lista ->
        lista.map { it.toDomain() }
    }

    suspend fun buscarPorId(id: Int): Pedido? = pedidoDao.buscarPorId(id)?.toDomain()

    suspend fun agregar(pedido: Pedido): Pedido {
        val idGenerado = pedidoDao.insertar(pedido.toEntity())
        return if (pedido.id == 0) {
            pedido.copy(id = idGenerado.toInt())
        } else {
            pedido
        }
    }

    suspend fun actualizar(pedido: Pedido) {
        pedidoDao.actualizar(pedido.toEntity())
    }

    suspend fun eliminar(id: Int): Boolean = pedidoDao.eliminarPorId(id) > 0
}
