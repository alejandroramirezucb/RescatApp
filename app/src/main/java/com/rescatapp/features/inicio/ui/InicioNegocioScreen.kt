package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja

@Composable
fun InicioNegocioScreen() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 20.dp)
    ) {
        item { EncabezadoNegocio() }
        item {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ResumenHoy()
                VentasSemana()
                PedidosRecientes()
            }
        }
    }
}

@Composable
private fun EncabezadoNegocio() {
    Column {
        Box(
            modifier = Modifier.fillMaxWidth().height(24.dp).background(Naranja)
        ) {
            Text(
                text = "Panadería La Central",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hoy es un buen día",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Buenos días,\nPanadería La Central",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun ResumenHoy() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Resumen de hoy",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaEstadistica(
                icono = "💰",
                valor = "Bs. 420",
                etiqueta = "Ventas",
                colorFondo = Color(0xFFFFF8F0),
                modifier = Modifier.weight(1f)
            )
            TarjetaEstadistica(
                icono = "🛍️",
                valor = "18",
                etiqueta = "Pedidos",
                colorFondo = Color(0xFFFFF8F0),
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TarjetaEstadistica(
                icono = "🎁",
                valor = "6",
                etiqueta = "Ofertas activas",
                colorFondo = Color(0xFFF0F8EC),
                modifier = Modifier.weight(1f)
            )
            TarjetaEstadistica(
                icono = "🌱",
                valor = "24",
                etiqueta = "Rescatados",
                colorFondo = Color(0xFFF0F8EC),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TarjetaEstadistica(
    icono: String,
    valor: String,
    etiqueta: String,
    colorFondo: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = icono, style = MaterialTheme.typography.titleLarge)
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Naranja
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun VentasSemana() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ventas · Semana actual",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val dias = listOf("L", "M", "X", "J", "V", "S", "D")
                dias.forEachIndexed { index, dia ->
                    val esHoy = index == 5
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(width = 32.dp, height = if (esHoy) 40.dp else 20.dp)
                                .background(
                                    color = if (esHoy) Naranja else Naranja.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = dia,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (esHoy) {
                                Naranja
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (esHoy) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PedidosRecientes() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Pedidos recientes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TarjetaPedidoReciente(
            id = "#APV-1024",
            producto = "Pack Sorpresa",
            cliente = "XXX.",
            hora = "17:42",
            precio = "Bs.25",
            estado = "Pendiente",
            colorEstado = Naranja
        )
        TarjetaPedidoReciente(
            id = "#APV-1023",
            producto = "Pack Sorpresa",
            cliente = "XXX.",
            hora = "17:30",
            precio = "Bs.25",
            estado = "Listo",
            colorEstado = Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun TarjetaPedidoReciente(
    id: String,
    producto: String,
    cliente: String,
    hora: String,
    precio: String,
    estado: String,
    colorEstado: Color
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "$id · $producto",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = cliente,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "·",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = hora,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = colorEstado.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(colorEstado, CircleShape))
                        Text(
                            estado,
                            style = MaterialTheme.typography.labelSmall,
                            color = colorEstado,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Text(
                text = precio,
                style = MaterialTheme.typography.titleMedium,
                color = Naranja,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
