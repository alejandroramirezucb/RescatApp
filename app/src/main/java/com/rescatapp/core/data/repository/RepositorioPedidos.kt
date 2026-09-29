package com.rescatapp.core.data.repository

import com.rescatapp.core.model.Pedido
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.StateFlow

@Singleton
class RepositorioPedidos @Inject constructor() : PedidoRepositoryEnMemoria() {
    val pedidos: StateFlow<List<Pedido>> get() = elementos

    fun agregarPedido(nuevoPedido: Pedido): Pedido = agregar(nuevoPedido)
}
