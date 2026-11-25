package com.rescatapp.features.detalle.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BotonSecundario
import com.rescatapp.core.designsystem.components.MapaUbicacion
import com.rescatapp.core.designsystem.components.TextoDisponibilidad
import com.rescatapp.core.designsystem.theme.bordeTarjeta
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.formatearHorario
import com.rescatapp.core.util.formatearPeso

@Composable
internal fun RetiroOferta(oferta: Oferta) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = bordeTarjeta()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Para tu retiro", style = MaterialTheme.typography.titleMedium)
            DatoRetiroOferta(
                Icons.Outlined.AccessTime,
                "Horario de retiro",
                formatearHorario(
                    oferta.fechaRetiro,
                    oferta.horaRetiroDesde,
                    oferta.horaRetiroHasta
                )
            )
            DatoRetiroOferta(
                Icons.Outlined.Scale,
                "Peso del pack",
                oferta.pesoKg.formatearPeso()
            )
            if (oferta.latitud != null && oferta.longitud != null) {
                UbicacionComercio(oferta.comercio, oferta.latitud, oferta.longitud)
            }
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

@Composable
private fun UbicacionComercio(comercio: String, latitud: Double, longitud: Double) {
    val contexto = LocalContext.current
    MapaUbicacion(latitud = latitud, longitud = longitud)
    BotonSecundario(
        texto = "Cómo llegar",
        onClick = {
            val destino = Uri.parse("geo:0,0?q=$latitud,$longitud(${Uri.encode(comercio)})")
            runCatching { contexto.startActivity(Intent(Intent.ACTION_VIEW, destino)) }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
