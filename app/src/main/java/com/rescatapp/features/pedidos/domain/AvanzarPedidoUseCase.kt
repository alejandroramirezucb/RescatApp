package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class AvanzarPedidoUseCase @Inject constructor(private val repositorioPedidos: RepositorioPedidos) {
    suspend operator fun invoke(pedidoId: Int, codigoRescate: String? = null): ResultadoOperacion {
        val pedido = repositorioPedidos.buscarPorId(pedidoId)
            ?: return ResultadoOperacion.Error("Pedido no encontrado")
        return repositorioPedidos.avanzar(pedido, codigoRescate)
    }
}
