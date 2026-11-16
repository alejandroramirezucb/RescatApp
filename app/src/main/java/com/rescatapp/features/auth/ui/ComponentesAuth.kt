package com.rescatapp.features.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rescatapp.core.designsystem.components.BarraSuperior
import com.rescatapp.core.designsystem.components.BotonPrincipal

@Composable
internal fun PantallaAuth(
    titulo: String,
    onVolver: () -> Unit,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Scaffold(topBar = { BarraSuperior(titulo = titulo, onVolver = onVolver) }) { espacioInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(espacioInterno)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = contenido
        )
    }
}

@Composable
internal fun MensajeErrorAuth(mensaje: String?) {
    if (mensaje == null) return
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
internal fun BotonEnviarAuth(
    texto: String,
    cargando: Boolean,
    habilitado: Boolean,
    onClick: () -> Unit
) {
    if (cargando) {
        Box(
            modifier = Modifier.fillMaxWidth().height(52.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        BotonPrincipal(texto = texto, onClick = onClick, habilitado = habilitado)
    }
}

@Composable
internal fun EnlaceAuth(pregunta: String, accion: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pregunta,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onClick) { Text(accion) }
    }
}
