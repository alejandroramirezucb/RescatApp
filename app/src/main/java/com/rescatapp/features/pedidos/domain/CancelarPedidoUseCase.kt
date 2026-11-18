package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class CancelarPedidoUseCase @Inject constructor(
    private val repositorioPedidos: RepositorioPedidos,
    private val repositorioOfertas: RepositorioOfertas
) {
    suspend operator fun invoke(pedidoId: Int): ResultadoOperacion {
        val resultado = repositorioPedidos.cancelar(pedidoId)
        runCatching { repositorioOfertas.sincronizar() }
        return resultado
    }
}
