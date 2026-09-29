package com.rescatapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

enum class DestinoPrincipal(
    val ruta: String,
    val rutaRegistrada: String,
    val etiqueta: String,
    val icono: ImageVector
) {
    INICIO(Rutas.INICIO, Rutas.INICIO, "Inicio", Icons.Outlined.Home),
    EXPLORAR(Rutas.EXPLORAR, Rutas.EXPLORAR_POR_CATEGORIA, "Explorar", Icons.Outlined.Search),
    PEDIDOS(Rutas.PEDIDOS, Rutas.PEDIDOS, "Pedidos", Icons.Outlined.ShoppingBag),
    PERFIL(Rutas.PERFIL, Rutas.PERFIL, "Perfil", Icons.Outlined.Person);

    val muestraBotonPublicar: Boolean
        get() = this == INICIO || this == EXPLORAR

    companion object {
        fun desdeRuta(rutaActual: String?): DestinoPrincipal? =
            entries.find { it.rutaRegistrada == rutaActual }
    }
}
