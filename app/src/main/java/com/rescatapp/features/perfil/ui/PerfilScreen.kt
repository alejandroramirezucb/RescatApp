package com.rescatapp.features.perfil.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.FilaImpacto
import com.rescatapp.core.designsystem.theme.Naranja
import com.rescatapp.core.designsystem.theme.Verde
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo

@Composable
fun PerfilScreen(
    onVerNotificaciones: () -> Unit = {},
    onIrALogin: () -> Unit = {},
    onIrARegistro: () -> Unit = {},
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val rol by UsuarioDemo.rol.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        EncabezadoPerfil(estado.nombre, estado.correo, estado.inicial, estado.esNegocio)
        IndicadoresPerfil(estado)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InformacionCuenta(estado)
            SeccionGestionSesion(
                estado = estado,
                onIrALogin = onIrALogin,
                onIrARegistro = onIrARegistro,
                onCerrarSesion = viewModel::cerrarSesion
            )
            PreferenciasPerfil(onVerNotificaciones = onVerNotificaciones)
            SeccionSostenibilidadODS()
        }
        Spacer(Modifier.height(8.dp))
        BotonCambiarRol(rol)
        Spacer(Modifier.height(48.dp))
    }
}

@Composable
private fun SeccionGestionSesion(
    estado: PerfilUiState,
    onIrALogin: () -> Unit,
    onIrARegistro: () -> Unit,
    onCerrarSesion: () -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cuenta y Autenticación",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                if (estado.estaAutenticado) {
                    Text(
                        text = if (estado.esNegocio) "Negocio" else "Cliente",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (estado.esNegocio) Naranja else Verde,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (estado.estaAutenticado) {
                Text(
                    text = "Sesión iniciada como ${estado.nombre} (${estado.correo}).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onCerrarSesion,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Cerrar sesión")
                    }

                    TextButton(
                        onClick = onIrALogin,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cambiar cuenta")
                    }
                }
            } else {
                Text(
                    text = "Inicia sesión o regístrate para sincronizar " +
                        "tus pedidos y gestionar tu perfil.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onIrALogin,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Iniciar sesión")
                    }

                    OutlinedButton(
                        onClick = onIrARegistro,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text("Registrarse")
                    }
                }
            }
        }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(if (esNegocio) "Cambiar a perfil de cliente" else "Cambiar a perfil de negocio (Demo)")
    }
}
