package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.TextoPrincipal
import com.rescatapp.core.model.Categoria

@Composable
fun ImagenCategoria(
    categoria: Categoria,
    modifier: Modifier = Modifier,
    tamanoIcono: Dp = 40.dp,
    contenidoSuperpuesto: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier.background(categoria.colorFondo),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = categoria.icono,
            contentDescription = categoria.etiqueta,
            tint = TextoPrincipal.copy(alpha = 0.7f),
            modifier = Modifier.size(tamanoIcono)
        )
        contenidoSuperpuesto()
    }
}
