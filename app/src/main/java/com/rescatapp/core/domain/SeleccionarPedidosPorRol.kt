package com.rescatapp.core.domain

import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo

fun seleccionarPedidosPorRol(
    pedidos: List<Pedido>,
    rol: RolUsuario,
    nombreComercio: String? = null
): List<Pedido> = if (rol == RolUsuario.NEGOCIO) {
    val comercioFiltro = nombreComercio?.takeIf { it.isNotBlank() } ?: UsuarioDemo.negocioActual
    pedidos.filter {
        it.codigoServidor != null ||
            it.comercio.equals(comercioFiltro, ignoreCase = true)
    }
} else {
    pedidos
}
