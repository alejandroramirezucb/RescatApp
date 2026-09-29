package com.rescatapp.core.domain

import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.ImpactoSemanal
import com.rescatapp.core.model.Pedido
import javax.inject.Inject

private const val RESERVAS_BASE = 4
private const val AHORRO_BASE = 102.0
private const val PESO_BASE_KG = 3.6

class CalcularImpactoSemanalUseCase @Inject constructor() {
    operator fun invoke(pedidos: List<Pedido>): ImpactoSemanal = pedidos
        .filter { it.estado != EstadoPedido.CANCELADO }
        .fold(ImpactoSemanal(RESERVAS_BASE, AHORRO_BASE, PESO_BASE_KG)) { impacto, pedido ->
            impacto.copy(
                reservas = impacto.reservas + 1,
                ahorro = impacto.ahorro + pedido.ahorro,
                pesoKg = impacto.pesoKg + pedido.pesoKg
            )
        }
}
