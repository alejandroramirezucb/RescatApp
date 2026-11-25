package com.rescatapp.features.carrito.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.ItemCarrito
import com.rescatapp.core.util.formatearDinero

@Composable
internal fun TarjetaItemCarrito(
    item: ItemCarrito,
    onCambiarCantidad: (Int) -> Unit,
    onEliminar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.oferta.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = item.oferta.comercio,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Subtotal ${item.subtotal.formatearDinero()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(
                onClick = { onCambiarCantidad(item.cantidad - 1) },
                enabled = item.cantidad > 1
            ) {
                Icon(Icons.Outlined.Remove, contentDescription = "Quitar una unidad")
            }
            Text(item.cantidad.toString(), style = MaterialTheme.typography.titleMedium)
            IconButton(
                onClick = { onCambiarCantidad(item.cantidad + 1) },
                enabled = item.cantidad < item.oferta.cantidadDisponible
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Agregar una unidad")
            }
            IconButton(onClick = onEliminar) {
                Icon(Icons.Outlined.Delete, contentDescription = "Eliminar del carrito")
            }
        }
    }
}
