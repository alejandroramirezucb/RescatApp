package com.rescatapp.features.perfil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.FilaImpacto
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo

@Composable
fun PerfilScreen(viewModel: PerfilViewModel = hiltViewModel()) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val rol by UsuarioDemo.rol.collectAsStateWithLifecycle()
    Column(modifier = Modifier.fillMaxSize()) {
        EncabezadoPerfil(estado.nombre, estado.correo, estado.inicial, estado.esNegocio)
        IndicadoresPerfil(estado)
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InformacionCuenta(estado)
        }
        Spacer(Modifier.weight(1f))
        BotonCambiarRol(rol)
    }
}

@Composable
private fun IndicadoresPerfil(estado: PerfilUiState) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 1.dp) {
        FilaImpacto(
            impacto = estado.impacto,
            colorValores = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun BotonCambiarRol(rol: RolUsuario) {
    val esNegocio = rol == RolUsuario.NEGOCIO
    Button(
        onClick = {
            UsuarioDemo.cambiarRol(if (esNegocio) RolUsuario.CLIENTE else RolUsuario.NEGOCIO)
        },
        modifier = Modifier.fillMaxWidth().padding(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(if (esNegocio) "Cambiar a perfil de cliente" else "Cambiar a perfil de negocio (Demo)")
    }
}
