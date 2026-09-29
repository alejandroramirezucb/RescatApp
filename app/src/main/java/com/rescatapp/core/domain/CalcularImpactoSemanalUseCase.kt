package com.rescatapp.core.domain

import com.rescatapp.core.model.ImpactoSemanal
import com.rescatapp.core.model.Pedido
import javax.inject.Inject

class CalcularImpactoSemanalUseCase @Inject constructor(
    private val calcularImpacto: CalcularImpactoUseCase = CalcularImpactoUseCase()
) {
    operator fun invoke(pedidos: List<Pedido>): ImpactoSemanal = calcularImpacto(pedidos)
}
