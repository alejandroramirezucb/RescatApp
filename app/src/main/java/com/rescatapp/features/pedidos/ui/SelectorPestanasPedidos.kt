package com.rescatapp.features.pedidos.ui

import androidx.compose.runtime.Composable
import com.rescatapp.core.designsystem.components.SelectorPestanas

@Composable
internal fun SelectorPestanasPedidos(
    pestanaActual: PestanaPedidos,
    onPestanaSeleccionada: (PestanaPedidos) -> Unit
) {
    SelectorPestanas(
        titulos = PestanaPedidos.entries.map { it.titulo },
        indiceSeleccionado = pestanaActual.ordinal,
        onSeleccion = { onPestanaSeleccionada(PestanaPedidos.entries[it]) }
    )
}
