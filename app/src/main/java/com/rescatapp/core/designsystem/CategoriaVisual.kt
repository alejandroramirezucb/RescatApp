package com.rescatapp.core.designsystem

import androidx.compose.ui.graphics.Color
import com.rescatapp.core.model.Categoria

data class CategoriaVisual(val simbolo: String, val color: Color)

fun Categoria.visual(): CategoriaVisual = when (this) {
    Categoria.PANADERIA -> CategoriaVisual("🥐", Color(0xFFF5D7BE))
    Categoria.COMIDA -> CategoriaVisual("🍽️", Color(0xFFDCEBD8))
    Categoria.POSTRES -> CategoriaVisual("🧁", Color(0xFFF3DCE5))
    Categoria.CAFETERIA -> CategoriaVisual("☕", Color(0xFFDAE8EC))
}
