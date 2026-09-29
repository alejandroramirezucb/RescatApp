package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import javax.inject.Inject

class ConstruirProgresoPedidoUseCase @Inject constructor() {

    private val pasosFlujo = listOf(
        EstadoPedido.RESERVADO,
        EstadoPedido.PREPARANDO,
        EstadoPedido.LISTO,
        EstadoPedido.RECOGIDO
    )

    operator fun invoke(pedido: Pedido): List<PasoProgreso> = invoke(pedido.estado)

    operator fun invoke(estadoActual: EstadoPedido): List<PasoProgreso> {
        val indiceActual = pasosFlujo.indexOf(estadoActual)

        return pasosFlujo.mapIndexed { index, estado ->
            val esActual = (indiceActual >= 0 && index == indiceActual)
            val estaCompletado = (indiceActual >= 0 && index <= indiceActual)

            PasoProgreso(
                estado = estado.etiqueta,
                completado = estaCompletado,
                actual = esActual
            )
        }
    }
}
