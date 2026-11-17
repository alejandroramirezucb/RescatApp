package com.rescatapp.core.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDinero

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PreciosOferta(
    oferta: Oferta,
    estiloPrecioRescate: TextStyle = MaterialTheme.typography.titleSmall
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
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
