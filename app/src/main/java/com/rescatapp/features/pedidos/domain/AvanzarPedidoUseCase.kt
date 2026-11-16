package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.notification.GestorNotificaciones
import javax.inject.Inject

class AvanzarPedidoUseCase @Inject constructor(
    private val repositorioPedidos: RepositorioPedidos,
    private val gestorNotificaciones: GestorNotificaciones? = null
) {
    suspend operator fun invoke(pedidoId: Int): ResultadoOperacion {
        val pedido = repositorioPedidos.buscarPorId(pedidoId)
            ?: return ResultadoOperacion.Error(MensajesPedidos.PEDIDO_NO_ENCONTRADO)
        val siguienteEstado = pedido.estado.siguiente()
            ?: return ResultadoOperacion.Error("El pedido ya está ${pedido.estado.etiqueta}")

        val pedidoActualizado = pedido.copy(estado = siguienteEstado)
        repositorioPedidos.actualizar(pedidoActualizado)

        gestorNotificaciones?.let {
            when (siguienteEstado) {
                EstadoPedido.PREPARANDO -> it.notificarPedidoEnPreparacion(pedidoActualizado)
                EstadoPedido.LISTO -> it.notificarPedidoListo(pedidoActualizado)
                EstadoPedido.RECOGIDO -> it.notificarPedidoRecogido(pedidoActualizado)
                else -> Unit
            }
        }

        return ResultadoOperacion.Exito(pedidoActualizado)
    }
}
