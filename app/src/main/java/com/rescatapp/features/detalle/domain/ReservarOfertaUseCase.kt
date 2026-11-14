package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.notification.GestorNotificaciones
import javax.inject.Inject

class ReservarOfertaUseCase @Inject constructor(
    private val repositorioOfertas: RepositorioOfertas,
    private val repositorioPedidos: RepositorioPedidos,
    private val gestorNotificaciones: GestorNotificaciones? = null
) {
    suspend operator fun invoke(ofertaId: Int): ResultadoOperacion {
        val oferta = repositorioOfertas.buscarPorId(ofertaId)
            ?: return ResultadoOperacion.Error("Oferta no encontrada")
        if (oferta.estaAgotada) return ResultadoOperacion.Error("Esta oferta está agotada")

        repositorioOfertas.cambiarUnidadesDisponibles(oferta.id, diferencia = -1)
        val pedido = repositorioPedidos.agregar(oferta.crearPedido(idPedido = 0))

        gestorNotificaciones?.notificarReservaConfirmadaCliente(pedido)
        gestorNotificaciones?.notificarNuevoPedidoVendedor(pedido)

        val ofertaActualizada = repositorioOfertas.buscarPorId(ofertaId)
        if (ofertaActualizada?.estaAgotada == true) {
            gestorNotificaciones?.notificarOfertaAgotada(ofertaActualizada)
        }

        return ResultadoOperacion.Exito(pedido)
    }
}
