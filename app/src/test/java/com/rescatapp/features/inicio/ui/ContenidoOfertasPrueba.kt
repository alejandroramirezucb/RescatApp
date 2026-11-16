package com.rescatapp.features.inicio.ui

import androidx.compose.runtime.Composable
import com.rescatapp.core.model.Oferta

@Composable
internal fun ContenidoPrueba(
    estado: OfertasNegocioUiState,
    onReintentar: () -> Unit = {},
    onPublicar: (Oferta) -> Unit = {},
    onEditar: (Oferta) -> Unit = {},
    onEliminar: (Oferta) -> Unit = {},
    onEliminarLocal: (Oferta, () -> Unit) -> Unit = { _, _ -> }
) {
    OfertasNegocioContenido(
        estado = estado,
        onSeleccion = {},
        onReintentar = onReintentar,
        onPublicar = onPublicar,
        onEditarLocal = { _, _ -> },
        onEditarRemota = onEditar,
        onEliminar = onEliminar,
        onEliminarLocal = onEliminarLocal,
        onReintentarOperacion = {},
        onLimpiarMensaje = {}
    )
}
