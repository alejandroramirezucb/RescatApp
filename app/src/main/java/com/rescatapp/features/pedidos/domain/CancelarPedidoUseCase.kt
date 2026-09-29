package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class CancelarPedidoUseCase @Inject constructor(
    private val repositorioPedidos: RepositorioPedidos,
    private val repositorioOfertas: RepositorioOfertas
) {
    operator fun invoke(pedidoId: Int): ResultadoOperacion {
        val pedido = repositorioPedidos.buscarPorId(pedidoId)
            ?: return ResultadoOperacion.Error(MensajesPedidos.PEDIDO_NO_ENCONTRADO)
        if (pedido.estado != EstadoPedido.RESERVADO) {
            return ResultadoOperacion.Error(MensajesPedidos.SOLO_SE_CANCELA_RESERVADO)
        }

        val pedidoCancelado = pedido.copy(estado = EstadoPedido.CANCELADO)
        repositorioPedidos.actualizar(pedidoCancelado)
        repositorioOfertas.cambiarUnidadesDisponibles(pedido.ofertaId, diferencia = 1)
        return ResultadoOperacion.Exito(pedidoCancelado)
    }
}
