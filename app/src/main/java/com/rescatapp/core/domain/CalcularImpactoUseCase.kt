package com.rescatapp.core.domain

import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import javax.inject.Inject

class CalcularImpactoUseCase @Inject constructor() {
    operator fun invoke(pedidos: List<Pedido>): Impacto {
        val pedidosVigentes = pedidos.filter { it.estado != EstadoPedido.CANCELADO }
        return Impacto(
            rescates = pedidosVigentes.count(),
            ahorrado = pedidosVigentes.sumOf { it.ahorro },
            kgAprovechados = pedidosVigentes.sumOf { it.pesoKg }
        )
    }
}
