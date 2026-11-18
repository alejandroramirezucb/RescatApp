package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AvisoTemporal(mensaje: String?, onMostrado: () -> Unit, modifier: Modifier = Modifier) {
    val estado = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        if (mensaje != null) {
            estado.showSnackbar(mensaje)
            onMostrado()
        }
    }
    SnackbarHost(estado, modifier.padding(16.dp)) { datos ->
        Snackbar(snackbarData = datos, shape = MaterialTheme.shapes.medium)
    }
}
