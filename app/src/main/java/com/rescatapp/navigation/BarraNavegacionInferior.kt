package com.rescatapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.TextoGrisRescat

enum class DestinoPrincipal(
    val ruta: String,
    val rutaPatron: String,
    val etiqueta: String,
    val icono: ImageVector
) {
    INICIO("inicio", "inicio", "Inicio", Icons.Outlined.Home),
    EXPLORAR("explorar", "explorar?categoria={categoria}", "Explorar", Icons.Outlined.Search),
    PEDIDOS("pedidos", "pedidos", "Pedidos", Icons.Outlined.ShoppingBag),
    PERFIL("perfil", "perfil", "Perfil", Icons.Outlined.Person)
}

@Composable
fun BarraNavegacionInferior(
    destinoActual: DestinoPrincipal?,
    onDestinoSeleccionado: (DestinoPrincipal) -> Unit
) {
    NavigationBar(
        modifier = Modifier.height(80.dp),
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        DestinoPrincipal.entries.forEach { destino ->
            val seleccionado = destino == destinoActual
            val color = if (seleccionado) NaranjaRescat else TextoGrisRescat

            Column(
                modifier = Modifier.weight(1f).fillMaxHeight().clickable {
                    onDestinoSeleccionado(destino)
                },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = destino.icono,
                    contentDescription = destino.etiqueta,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = destino.etiqueta,
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                )
                Box(
                    modifier = Modifier.padding(top = 2.dp)
                        .width(32.dp)
                        .height(3.dp)
                        .background(if (seleccionado) NaranjaRescat else Color.Transparent)
                )
            }
        }
    }
}
