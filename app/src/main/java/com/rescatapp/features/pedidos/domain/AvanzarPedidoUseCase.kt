package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class AvanzarPedidoUseCase @Inject constructor(
    private val repositorioPedidos: RepositorioPedidos
) {
    operator fun invoke(pedido: Pedido): ResultadoOperacion<Pedido> {
        val siguienteEstado = pedido.estado.siguiente()
            ?: return ResultadoOperacion.Error("No se puede avanzar un pedido en estado ${pedido.estado.etiqueta}")

        val pedidoActualizado = pedido.copy(estado = siguienteEstado)
        repositorioPedidos.actualizar(pedidoActualizado)
        return ResultadoOperacion.Exito(pedidoActualizado)
    }

    operator fun invoke(pedidoId: Int): ResultadoOperacion<Pedido> {
        val pedido = repositorioPedidos.obtenerActualPorId(pedidoId)
            ?: return ResultadoOperacion.Error("Pedido no encontrado")
        return invoke(pedido)
    }
}
