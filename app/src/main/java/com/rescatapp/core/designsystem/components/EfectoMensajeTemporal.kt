package com.rescatapp.core.designsystem.components

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun EfectoMensajeTemporal(
    mensaje: String?,
    estadoSnackbar: SnackbarHostState,
    onMensajeMostrado: () -> Unit
) {
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            estadoSnackbar.showSnackbar(mensaje)
            onMensajeMostrado()
        }
    }
}
