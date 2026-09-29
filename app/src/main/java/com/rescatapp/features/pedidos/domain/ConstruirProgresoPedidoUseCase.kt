package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.PasoProgreso
import javax.inject.Inject

class ConstruirProgresoPedidoUseCase @Inject constructor() {
    private val pasosDelRetiro = listOf(
        EstadoPedido.RESERVADO,
        EstadoPedido.PREPARANDO,
        EstadoPedido.LISTO,
        EstadoPedido.RECOGIDO
    )

    operator fun invoke(estadoActual: EstadoPedido): List<PasoProgreso> {
        val indiceActual = pasosDelRetiro.indexOf(estadoActual)
        return pasosDelRetiro.mapIndexed { indice, paso ->
            PasoProgreso(
                etiqueta = paso.etiqueta,
                completado = indice <= indiceActual,
                actual = indice == indiceActual
            )
        }
    }
}
