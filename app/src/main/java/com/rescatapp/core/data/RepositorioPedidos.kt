package com.rescatapp.core.data

import com.rescatapp.core.data.local.PedidoDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.Pedido
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class RepositorioPedidos @Inject constructor(
    private val pedidoDao: PedidoDao,
    private val supabase: com.rescatapp.core.data.supabase.SupabaseConexion? = null
) {
    val conectado: Boolean get() = supabase != null
    val pedidos: Flow<List<Pedido>> = supabase?.pedidos ?: pedidoDao.observarTodos().map { lista ->
        lista.map { it.toDomain() }
    }

    suspend fun buscarPorId(id: Int): Pedido? = if (supabase !=
        null
    ) {
        pedidos.first().find { it.id == id }
    } else {
        pedidoDao.buscarPorId(id)?.toDomain()
    }

    suspend fun reservar(ofertaId: Int) = checkNotNull(supabase).reservar(ofertaId)
    suspend fun cancelar(id: Int) = checkNotNull(supabase).cancelar(id)
    suspend fun avanzar(pedido: Pedido, codigo: String?) =
        checkNotNull(supabase).avanzar(pedido.id, pedido.estado.name, codigo)

    suspend fun agregar(pedido: Pedido): Pedido {
        check(supabase == null) { "Los pedidos se crean mediante reservar_oferta." }
        val idGenerado = pedidoDao.insertar(pedido.toEntity())
        return if (pedido.id == 0) {
            pedido.copy(id = idGenerado.toInt())
        } else {
            pedido
        }
    }

    suspend fun actualizar(pedido: Pedido) {
        check(supabase == null) { "Usa las operaciones seguras del servidor." }
        pedidoDao.actualizar(pedido.toEntity())
    }

    suspend fun eliminar(id: Int): Boolean {
        check(supabase == null) { "El historial de pedidos se conserva en el servidor." }
        return pedidoDao.eliminarPorId(id) > 0
    }
}
