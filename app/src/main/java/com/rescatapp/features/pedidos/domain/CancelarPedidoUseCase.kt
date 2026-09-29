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
            ?: return ResultadoOperacion.Error("Pedido no encontrado")
        if (pedido.estado != EstadoPedido.RESERVADO) {
            return ResultadoOperacion.Error("Solo puedes cancelar un pedido reservado")
        }

        val pedidoCancelado = pedido.copy(estado = EstadoPedido.CANCELADO)
        repositorioPedidos.actualizar(pedidoCancelado)
        repositorioOfertas.buscarPorId(pedido.ofertaId)?.let { oferta ->
            repositorioOfertas.actualizar(
                oferta.copy(cantidadDisponible = oferta.cantidadDisponible + 1)
            )
        }
        return ResultadoOperacion.Exito(pedidoCancelado)
    }
}
