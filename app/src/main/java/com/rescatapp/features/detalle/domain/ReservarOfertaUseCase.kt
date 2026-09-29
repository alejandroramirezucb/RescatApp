package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class ReservarOfertaUseCase @Inject constructor(
    private val ofertas: RepositorioOfertas,
    private val pedidos: RepositorioPedidos
) {
    operator fun invoke(ofertaId: Int): ResultadoOperacion {
        val oferta = ofertas.obtenerActualPorId(ofertaId)
            ?: return ResultadoOperacion.Error("Oferta no encontrada")
        if (oferta.estaAgotada) {
            return ResultadoOperacion.Error("Esta oferta está agotada")
        }

        ofertas.actualizar(oferta.copy(cantidadDisponible = oferta.cantidadDisponible - 1))
        val pedido = pedidos.agregar(
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
        return ResultadoOperacion.Exito(pedido)
    }
}
