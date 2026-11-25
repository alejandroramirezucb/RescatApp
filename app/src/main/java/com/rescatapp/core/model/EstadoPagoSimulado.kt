package com.rescatapp.core.model

enum class EstadoPagoSimulado {
    APROBADO,
    ANULADO;

    companion object {
        fun segun(estado: EstadoPedido): EstadoPagoSimulado =
            if (estado == EstadoPedido.CANCELADO) ANULADO else APROBADO
    }
}
