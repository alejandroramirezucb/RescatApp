package com.rescatapp.core.domain

import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import javax.inject.Inject

class CalcularImpactoUseCase @Inject constructor() {
    operator fun invoke(pedidos: List<Pedido>): Impacto {
        val validos = pedidos.filter { it.estado != EstadoPedido.CANCELADO }
        return Impacto(
            rescates = validos.count(),
            ahorrado = validos.sumOf { it.ahorro },
            kgAprovechados = validos.sumOf { it.pesoKg }
        )
    }
}
