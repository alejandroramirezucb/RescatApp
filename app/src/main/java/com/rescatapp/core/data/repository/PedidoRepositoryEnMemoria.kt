package com.rescatapp.core.data.repository

import com.rescatapp.core.data.mock.pedidosDeEjemplo
import com.rescatapp.core.model.Pedido
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class PedidoRepositoryEnMemoria @Inject constructor() : RepositorioEnMemoria<Pedido>(pedidosDeEjemplo) {
    override fun obtenerId(elemento: Pedido): Int = elemento.id
    override fun asignarId(elemento: Pedido, nuevoId: Int): Pedido = elemento.copy(id = nuevoId)
}
