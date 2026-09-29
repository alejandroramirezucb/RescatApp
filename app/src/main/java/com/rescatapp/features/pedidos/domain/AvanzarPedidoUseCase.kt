package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class AvanzarPedidoUseCase @Inject constructor(private val repositorioPedidos: RepositorioPedidos) {
    operator fun invoke(pedidoId: Int): ResultadoOperacion {
        val pedido = repositorioPedidos.buscarPorId(pedidoId)
            ?: return ResultadoOperacion.Error("Pedido no encontrado")
        val siguienteEstado = pedido.estado.siguiente()
            ?: return ResultadoOperacion.Error("El pedido ya está ${pedido.estado.etiqueta}")

        val pedidoActualizado = pedido.copy(estado = siguienteEstado)
        repositorioPedidos.actualizar(pedidoActualizado)
        return ResultadoOperacion.Exito(pedidoActualizado)
    }
}
