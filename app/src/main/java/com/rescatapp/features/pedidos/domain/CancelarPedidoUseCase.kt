package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class CancelarPedidoUseCase @Inject constructor(
    private val repositorioPedidos: RepositorioPedidos,
    private val repositorioOfertas: RepositorioOfertas
) {
    operator fun invoke(pedido: Pedido): ResultadoOperacion<Pedido> {
        if (pedido.estado != EstadoPedido.RESERVADO) {
            return ResultadoOperacion.Error("Solo puedes cancelar un pedido reservado")
        }

        val pedidoCancelado = pedido.copy(estado = EstadoPedido.CANCELADO)
        repositorioPedidos.actualizar(pedidoCancelado)

        val ofertaOriginal = repositorioOfertas.obtenerActualPorId(pedido.ofertaId)
        if (ofertaOriginal != null) {
            val ofertaActualizada = ofertaOriginal.copy(
                cantidadDisponible = ofertaOriginal.cantidadDisponible + 1
            )
            repositorioOfertas.actualizar(ofertaActualizada)
        }

        return ResultadoOperacion.Exito(pedidoCancelado)
    }

    operator fun invoke(pedidoId: Int): ResultadoOperacion<Pedido> {
        val pedido = repositorioPedidos.obtenerActualPorId(pedidoId)
            ?: return ResultadoOperacion.Error("Pedido no encontrado")
        return invoke(pedido)
    }
}
