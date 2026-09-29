package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero

@Composable
fun PreciosOferta(
    oferta: Oferta,
    estiloPrecioRescate: TextStyle = MaterialTheme.typography.titleSmall
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = oferta.precioNormal.formatearDinero(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textDecoration = TextDecoration.LineThrough
        )
        Text(
            text = oferta.precioRescate.formatearDinero(),
            style = estiloPrecioRescate,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
