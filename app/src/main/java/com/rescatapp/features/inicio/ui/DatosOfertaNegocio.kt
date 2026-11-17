package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero

@Composable
internal fun DatosOfertaNegocio(
    oferta: Oferta,
    onEditar: () -> Unit,
    habilitada: Boolean,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EncabezadoOferta(oferta.nombre, oferta.estaAgotada)
        Text(
            text = "Quedan: ${oferta.cantidadDisponible}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        PieOferta(oferta.precioRescate.formatearDinero(), onEditar, habilitada)
    }
}

@Composable
private fun EncabezadoOferta(nombre: String, estaAgotada: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = nombre,
            style = MaterialTheme.typography.titleMedium
        )
        ChipEstadoOferta(estaAgotada)
    }
}

@Composable
private fun PieOferta(precio: String, onEditar: () -> Unit, habilitada: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = precio,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        OutlinedButton(
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            onClick = onEditar,
            enabled = habilitada
        ) {
            Text(
                text = "Editar",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ChipEstadoOferta(estaAgotada: Boolean = false) {
    val esquema = MaterialTheme.colorScheme
    val color = if (estaAgotada) esquema.onErrorContainer else esquema.onSecondaryContainer
    val fondo = if (estaAgotada) esquema.errorContainer else esquema.secondaryContainer
    val texto = if (estaAgotada) "Agotada" else "Activa"
    Surface(color = fondo, shape = MaterialTheme.shapes.small) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(modifier = Modifier.size(6.dp).background(color, CircleShape))
            Text(
                text = texto,
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
