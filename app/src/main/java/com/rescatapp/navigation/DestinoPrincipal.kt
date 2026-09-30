package com.rescatapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector
import com.rescatapp.core.model.RolUsuario

enum class DestinoPrincipal(
    val ruta: String,
    val rutaRegistrada: String,
    val etiqueta: String,
    val icono: ImageVector
) {
    INICIO(Rutas.INICIO, Rutas.INICIO, "Inicio", Icons.Filled.Home),
    EXPLORAR(Rutas.EXPLORAR, Rutas.EXPLORAR_POR_CATEGORIA, "Explorar", Icons.Filled.Search),
    OFERTAS(Rutas.OFERTAS_NEGOCIO, Rutas.OFERTAS_NEGOCIO, "Ofertas", Icons.Filled.CardGiftcard),
    PEDIDOS(Rutas.PEDIDOS, Rutas.PEDIDOS, "Pedidos", Icons.Filled.ShoppingBag),
    PERFIL(Rutas.PERFIL, Rutas.PERFIL, "Perfil", Icons.Filled.Person);

    fun muestraBotonPublicar(rol: RolUsuario): Boolean = rol == RolUsuario.NEGOCIO && this == INICIO

    companion object {
        fun desdeRuta(rutaActual: String?): DestinoPrincipal? =
            entries.find { it.rutaRegistrada == rutaActual }

        fun paraRol(rol: RolUsuario): List<DestinoPrincipal> = if (rol == RolUsuario.NEGOCIO) {
            listOf(INICIO, OFERTAS, PEDIDOS, PERFIL)
        } else {
            listOf(INICIO, EXPLORAR, PEDIDOS, PERFIL)
        }
    }
}
