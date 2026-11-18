package com.rescatapp.features.inicio.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.runtime.Composable
import com.rescatapp.core.designsystem.components.DialogoConfirmacion
import com.rescatapp.core.model.Oferta

@Composable
internal fun DialogoEditarOferta(
    oferta: Oferta,
    onGuardar: (Oferta) -> Unit,
    onDescartar: () -> Unit
) {
    EditorOferta(oferta, onGuardar) { onConfirmar, contenido ->
        DialogoConfirmacion(
            titulo = "Editar oferta",
            textoConfirmar = "Guardar cambios",
            textoDescartar = "Cancelar",
            onConfirmar = onConfirmar,
            onDescartar = onDescartar,
            icono = Icons.Outlined.Edit,
            contenidoDesplazable = false,
            contenido = contenido
        )
    }
}
