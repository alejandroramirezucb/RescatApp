package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido

class ReservarOfertaUseCase(
    private val ofertas: RepositorioOfertas,
    private val pedidos: RepositorioPedidos
) {
    operator fun invoke(ofertaId: Int): Pedido? {
        val oferta = ofertas.obtenerPorId(ofertaId) ?: return null
        if (oferta.estaAgotada) return null

        ofertas.actualizar(oferta.copy(cantidadDisponible = oferta.cantidadDisponible - 1))
        return pedidos.agregar(
            Pedido(
                id = 0,
                ofertaId = oferta.id,
                nombreOferta = oferta.nombre,
                comercio = oferta.comercio,
                categoria = oferta.categoria,
                precioPagado = oferta.precioRescate,
                ahorro = oferta.ahorroPorUnidad,
                pesoKg = oferta.pesoKg,
                horaRetiroDesde = oferta.horaRetiroDesde,
                horaRetiroHasta = oferta.horaRetiroHasta,
                estado = EstadoPedido.RESERVADO
            )
        )
    }
}
