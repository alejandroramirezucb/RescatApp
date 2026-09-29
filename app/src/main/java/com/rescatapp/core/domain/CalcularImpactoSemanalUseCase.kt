package com.rescatapp.core.domain

import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ImpactoSemanal
import com.rescatapp.core.model.Pedido
import javax.inject.Inject

class CalcularImpactoSemanalUseCase @Inject constructor() {
    operator fun invoke(pedidos: List<Pedido>): ImpactoSemanal = pedidos
        .filter { it.estado != EstadoPedido.CANCELADO }
        .fold(ImpactoSemanal(0, 0.0, 0.0)) { impacto, pedido ->
            impacto.copy(
                reservas = impacto.reservas + 1,
                ahorro = impacto.ahorro + pedido.ahorro,
                pesoKg = impacto.pesoKg + pedido.pesoKg
            )
        }
}
