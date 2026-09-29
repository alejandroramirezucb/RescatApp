package com.rescatapp.core.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BakeryDining
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalPizza
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.rescatapp.core.model.Categoria

val iconoTodasLasCategorias: ImageVector = Icons.Outlined.RestaurantMenu

val Categoria.icono: ImageVector
    get() = when (this) {
        Categoria.PANADERIA -> Icons.Outlined.BakeryDining
        Categoria.COMIDA -> Icons.Outlined.LocalPizza
        Categoria.POSTRES -> Icons.Outlined.Cake
        Categoria.CAFETERIA -> Icons.Outlined.LocalCafe
    }

val Categoria.colorFondo: Color
    get() = when (this) {
        Categoria.PANADERIA -> Color(0xFFF5D7BE)
        Categoria.COMIDA -> Color(0xFFDCEBD8)
        Categoria.POSTRES -> Color(0xFFF3DCE5)
        Categoria.CAFETERIA -> Color(0xFFDAE8EC)
    }
