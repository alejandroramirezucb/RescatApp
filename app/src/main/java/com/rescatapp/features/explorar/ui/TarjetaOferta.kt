package com.rescatapp.features.explorar.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rescatapp.core.designsystem.NaranjaRescat
import com.rescatapp.core.designsystem.colorDisponibilidad
import com.rescatapp.core.designsystem.visual
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearDescuento
import com.rescatapp.core.util.formatearDinero
import com.rescatapp.core.util.formatearDisponibilidad

@Composable
fun TarjetaOferta(oferta: Oferta, onClick: () -> Unit) {
    val visual = oferta.categoria.visual()

    Card(
        modifier = Modifier.fillMaxWidth().height(264.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().height(108.dp).background(visual.color),
            contentAlignment = Alignment.Center
        ) {
            Text(visual.simbolo, fontSize = 46.sp)
            Surface(
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                color = NaranjaRescat,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    oferta.porcentajeDescuento.formatearDescuento(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                )
            }
        }
        Column(Modifier.fillMaxSize().padding(10.dp)) {
            Text(
                oferta.nombre,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                oferta.comercio,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    oferta.precioNormal.formatearDinero(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textDecoration = TextDecoration.LineThrough
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    oferta.precioRescate.formatearDinero(),
                    style = MaterialTheme.typography.titleSmall,
                    color = NaranjaRescat,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                oferta.cantidadDisponible.formatearDisponibilidad(),
                style = MaterialTheme.typography.labelSmall,
                color = oferta.disponibilidad.colorDisponibilidad(),
                fontWeight = FontWeight.Bold
            )
        }
    }
}
