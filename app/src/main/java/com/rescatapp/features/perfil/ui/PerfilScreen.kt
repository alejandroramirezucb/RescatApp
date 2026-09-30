package com.rescatapp.features.perfil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.FilaImpacto
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.NaranjaVivo
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo

@Composable
fun PerfilScreen(viewModel: PerfilViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val rol by UsuarioDemo.rol.collectAsStateWithLifecycle()
    val esNegocio = rol == RolUsuario.NEGOCIO

    Column(modifier = Modifier.fillMaxSize()) {
        EncabezadoPerfil(rol)
        Surface(color = MaterialTheme.colorScheme.surface) {
            FilaImpacto(
                impacto = estado.impacto,
                colorValores = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val nuevoRol = if (esNegocio) RolUsuario.CLIENTE else RolUsuario.NEGOCIO
                UsuarioDemo.cambiarRol(nuevoRol)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                if (esNegocio) {
                    "Cambiar a perfil de cliente"
                } else {
                    "Cambiar a perfil de negocio (Demo)"
                }
            )
        }
    }
}

@Composable
private fun EncabezadoPerfil(rol: RolUsuario) {
    val esNegocio = rol == RolUsuario.NEGOCIO
    val nombre = if (esNegocio) UsuarioDemo.NEGOCIO_DEMO else UsuarioDemo.NOMBRE
    val correo = if (esNegocio) "contacto@lacentral.com" else UsuarioDemo.CORREO
    val inicial = if (esNegocio) UsuarioDemo.NEGOCIO_DEMO.take(1) else UsuarioDemo.inicial

    Box(modifier = Modifier.fillMaxWidth().background(Naranja).clipToBounds()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-40).dp)
                .size(150.dp)
                .background(NaranjaVivo.copy(alpha = 0.35f), CircleShape)
        )
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inicial,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
            Column {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = correo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}
