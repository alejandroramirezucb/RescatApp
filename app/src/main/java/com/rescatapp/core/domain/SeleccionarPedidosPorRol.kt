package com.rescatapp.core.domain

import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo

fun seleccionarPedidosPorRol(pedidos: List<Pedido>, rol: RolUsuario): List<Pedido> =
    if (rol == RolUsuario.NEGOCIO) {
        pedidos.filter { it.comercio == UsuarioDemo.NEGOCIO_DEMO }
    } else {
        pedidos
    }
