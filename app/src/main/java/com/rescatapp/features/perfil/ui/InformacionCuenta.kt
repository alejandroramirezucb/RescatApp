package com.rescatapp.features.perfil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun InformacionCuenta(estado: PerfilUiState) {
    TarjetaPerfil(titulo = "Información de la cuenta") {
        FilaDatoPerfil(
            icono = if (estado.esNegocio) Icons.Default.Business else Icons.Default.Person,
            etiqueta = if (estado.esNegocio) "Comercio" else "Usuario",
            valor = estado.nombre
        )
        FilaDatoPerfil(Icons.Default.Email, "Correo", estado.correo)
    }
}

@Composable
internal fun PreferenciasPerfil() {
    TarjetaPerfil(titulo = "Preferencias y alertas") {
        FilaDatoPerfil(
            icono = Icons.Default.Notifications,
            etiqueta = "Notificaciones",
            valor = "Alertas inmediatas al publicar ofertas cercanas"
        )
        FilaDatoPerfil(
            icono = Icons.Default.Tune,
            etiqueta = "Radio de rescate",
            valor = "Hasta 5 km a la redonda"
        )
    }
}

@Composable
private fun TarjetaPerfil(titulo: String, contenido: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = titulo, style = MaterialTheme.typography.titleSmall)
            contenido()
        }
    }
}

@Composable
private fun FilaDatoPerfil(icono: ImageVector, etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = valor,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
