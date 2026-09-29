package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class ReservarOfertaUseCase @Inject constructor(
    private val repositorioOfertas: RepositorioOfertas,
    private val repositorioPedidos: RepositorioPedidos
) {
    operator fun invoke(ofertaId: Int): ResultadoOperacion {
        val oferta = repositorioOfertas.buscarPorId(ofertaId)
            ?: return ResultadoOperacion.Error("Oferta no encontrada")
        if (oferta.estaAgotada) return ResultadoOperacion.Error("Esta oferta está agotada")

        repositorioOfertas.actualizar(
            oferta.copy(cantidadDisponible = oferta.cantidadDisponible - 1)
        )
        val pedido = repositorioPedidos.agregar(oferta.crearPedido(idPedido = 0))
        return ResultadoOperacion.Exito(pedido)
    }
}
