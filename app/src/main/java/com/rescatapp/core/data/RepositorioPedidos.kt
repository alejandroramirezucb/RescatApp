package com.rescatapp.core.data

import com.rescatapp.core.data.local.PedidoDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.data.remote.PedidoDto
import com.rescatapp.core.data.remote.SupabaseApi
import com.rescatapp.core.data.remote.mensajeDeError
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RepositorioPedidos @Inject constructor(
    private val api: SupabaseApi,
    private val pedidoDao: PedidoDao,
    private val autenticacion: RepositorioAutenticacion
) {
    val pedidos: Flow<List<Pedido>> = pedidoDao.observarTodos().map { lista ->
        lista.map { it.toDomain() }
    }

    suspend fun buscarPorId(id: Int): Pedido? = pedidoDao.buscarPorId(id)?.toDomain()

    suspend fun sincronizar() {
        val token = autenticacion.token()
        val remotos = if (token == null) emptyList() else api.obtenerPedidos(token)
        pedidoDao.insertarTodos(remotos.map { it.toDomain().toEntity() })
        pedidoDao.eliminarExcepto(remotos.map { it.id })
    }

    suspend fun reservar(ofertaId: Int) = operar { token ->
        api.reservarOferta(
            token,
            mapOf("p_oferta_id" to ofertaId, "p_solicitud_id" to UUID.randomUUID().toString())
        )
    }

    suspend fun cancelar(id: Int) = operar { token ->
        api.cancelarPedido(token, mapOf("p_pedido_id" to id))
    }

    suspend fun avanzar(pedido: Pedido, codigo: String?) = operar { token ->
        api.avanzarPedido(
            token,
            mapOf(
                "p_pedido_id" to pedido.id,
                "p_estado_esperado" to pedido.estado.name,
                "p_codigo_rescate" to codigo
            )
        )
    }

    private suspend fun operar(accion: suspend (String) -> List<PedidoDto>): ResultadoOperacion =
        try {
            val pedido = accion(autenticacion.tokenObligatorio()).first().toDomain()
            runCatching { sincronizar() }
            ResultadoOperacion.Exito(pedido)
        } catch (error: Exception) {
            ResultadoOperacion.Error(mensajeDeError(error))
        }
}
