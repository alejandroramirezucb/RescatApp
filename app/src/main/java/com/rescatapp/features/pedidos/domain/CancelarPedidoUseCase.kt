package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject
import kotlinx.coroutines.CancellationException

class CancelarPedidoUseCase @Inject constructor(
    private val repositorioPedidos: RepositorioPedidos,
    private val repositorioOfertas: RepositorioOfertas
) {
    suspend operator fun invoke(pedidoId: Int): ResultadoOperacion {
        val pedido = repositorioPedidos.buscarPorId(pedidoId)
            ?: return ResultadoOperacion.Error("Pedido no encontrado")
        if (pedido.estado != EstadoPedido.RESERVADO && pedido.estado != EstadoPedido.CANCELADO) {
            return ResultadoOperacion.Error("Solo puedes cancelar un pedido reservado")
        }
        val resultado = repositorioPedidos.cancelar(pedidoId)
        try {
            if (resultado is ResultadoOperacion.Exito) {
                repositorioOfertas.sincronizar()
            } else {
                repositorioPedidos.sincronizar()
            }
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: Exception) {
            // El resultado confirmado por el RPC prevalece sobre un fallo de sincronización.
        }
        return resultado
    }
}
