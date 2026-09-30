package com.rescatapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.NaranjaVivo
import com.rescatapp.core.designsystem.theme.TextoSecundario
import com.rescatapp.core.model.RolUsuario

@Composable
fun BarraNavegacionInferior(
    destinoActual: DestinoPrincipal,
    rol: RolUsuario,
    onDestinoSeleccionado: (DestinoPrincipal) -> Unit
) {
    Surface(shadowElevation = 12.dp) {
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
            DestinoPrincipal.paraRol(rol).forEach { destino ->
                val seleccionado = destino == destinoActual
                NavigationBarItem(
                    selected = seleccionado,
                    onClick = { onDestinoSeleccionado(destino) },
                    icon = { Icon(destino.icono, contentDescription = null) },
                    label = { EtiquetaDestino(destino.etiqueta, seleccionado) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Naranja,
                        selectedTextColor = Naranja,
                        unselectedIconColor = TextoSecundario,
                        unselectedTextColor = TextoSecundario,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
private fun EtiquetaDestino(etiqueta: String, seleccionado: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
        )
        Box(
            modifier = Modifier
                .size(width = 20.dp, height = 3.dp)
                .background(
                    color = if (seleccionado) NaranjaVivo else Color.Transparent,
                    shape = RoundedCornerShape(2.dp)
                )
        )
    }
}
