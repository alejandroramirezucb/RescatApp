package com.rescatapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun BarraNavegacionInferior(
    destinoActual: DestinoPrincipal,
    onDestinoSeleccionado: (DestinoPrincipal) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        DestinoPrincipal.entries.forEach { destino ->
            NavigationBarItem(
                selected = destino == destinoActual,
                onClick = { onDestinoSeleccionado(destino) },
                icon = { Icon(destino.icono, contentDescription = null) },
                label = { Text(destino.etiqueta) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}
