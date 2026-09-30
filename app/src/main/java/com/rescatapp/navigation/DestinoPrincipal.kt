package com.rescatapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.ui.graphics.vector.ImageVector

enum class DestinoPrincipal(
    val ruta: String,
    val rutaRegistrada: String,
    val etiqueta: String,
    val icono: ImageVector
) {
    INICIO(Rutas.INICIO, Rutas.INICIO, "Inicio", Icons.Filled.Home),
    EXPLORAR(Rutas.EXPLORAR, Rutas.EXPLORAR_POR_CATEGORIA, "Explorar", Icons.Filled.Search),
    PEDIDOS(Rutas.PEDIDOS, Rutas.PEDIDOS, "Pedidos", Icons.Filled.ShoppingBag),
    PERFIL(Rutas.PERFIL, Rutas.PERFIL, "Perfil", Icons.Filled.Person);

    val muestraBotonPublicar: Boolean
        get() = this == INICIO || this == EXPLORAR

    companion object {
        fun desdeRuta(rutaActual: String?): DestinoPrincipal? =
            entries.find { it.rutaRegistrada == rutaActual }
    }
}
