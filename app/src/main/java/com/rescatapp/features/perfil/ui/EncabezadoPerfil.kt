package com.rescatapp.features.perfil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.NaranjaVivo

@Composable
internal fun EncabezadoPerfil(nombre: String, correo: String, inicial: String, esNegocio: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth().background(Naranja).clipToBounds()
    ) {
        Box(
            modifier = Modifier.align(Alignment.TopEnd).offset(x = 40.dp, y = (-40).dp)
                .size(150.dp).background(NaranjaVivo.copy(alpha = 0.35f), CircleShape)
        )
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AvatarPerfil(inicial)
            DatosEncabezadoPerfil(nombre, correo, esNegocio)
        }
    }
}

@Composable
private fun AvatarPerfil(inicial: String) {
    Box(
        modifier = Modifier.size(64.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(inicial, style = MaterialTheme.typography.headlineSmall, color = Color.White)
    }
}

@Composable
private fun DatosEncabezadoPerfil(nombre: String, correo: String, esNegocio: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = 0.25f)) {
            Text(
                text = if (esNegocio) "Rol: Negocio" else "Rol: Cliente",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Text(
            text = nombre,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = correo,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}
