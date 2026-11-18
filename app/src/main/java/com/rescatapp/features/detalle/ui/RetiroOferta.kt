package com.rescatapp.features.detalle.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.TextoDisponibilidad
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearHorario
import com.rescatapp.core.util.formatearPeso

@Composable
internal fun RetiroOferta(oferta: Oferta) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Para tu retiro", style = MaterialTheme.typography.titleMedium)
            DatoRetiroOferta(
                Icons.Outlined.AccessTime,
                "Horario de retiro",
                "${oferta.fechaRetiro} | ${formatearHorario(
                    oferta.horaRetiroDesde,
                    oferta.horaRetiroHasta
                )}"
            )
            DatoRetiroOferta(
                Icons.Outlined.Scale,
                "Peso del pack",
                oferta.pesoKg.formatearPeso()
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            TextoDisponibilidad(oferta)
        }
    }
}

@Composable
private fun DatoRetiroOferta(icono: ImageVector, etiqueta: String, valor: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = MaterialTheme.shapes.small
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(10.dp).size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(text = valor, style = MaterialTheme.typography.titleSmall)
        }
    }
}
