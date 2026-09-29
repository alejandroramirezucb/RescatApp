package com.rescatapp.features.pedidos.domain

import com.rescatapp.core.data.RepositorioPedidos
import javax.inject.Inject

class ObtenerPedidosUseCase @Inject constructor(private val repositorio: RepositorioPedidos) {
    fun obtener() = repositorio.pedidos
}
