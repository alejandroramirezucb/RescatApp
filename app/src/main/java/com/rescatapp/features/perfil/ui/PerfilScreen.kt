package com.rescatapp.features.perfil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.TarjetaImpacto
import com.rescatapp.core.designsystem.theme.RescatAppTheme

@Composable
fun PerfilScreen(
    onVerNotificaciones: () -> Unit = {},
    onIrALogin: () -> Unit = {},
    onIrARegistro: () -> Unit = {},
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    PerfilContenido(
        estado = estado,
        onVerNotificaciones = onVerNotificaciones,
        onIrALogin = onIrALogin,
        onIrARegistro = onIrARegistro,
        onCerrarSesion = viewModel::cerrarSesion
    )
}

@Composable
internal fun PerfilContenido(
    estado: PerfilUiState,
    onVerNotificaciones: () -> Unit,
    onIrALogin: () -> Unit,
    onIrARegistro: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            EncabezadoPerfil(estado.nombre, estado.correo, estado.inicial, estado.esNegocio)
            TarjetaImpacto(titulo = "Tu impacto", impacto = estado.impacto)
            InformacionCuenta(estado)
            SesionPerfil(
                estado = estado,
                onIrALogin = onIrALogin,
                onIrARegistro = onIrARegistro,
                onCerrarSesion = onCerrarSesion
            )
            PreferenciasPerfil(onVerNotificaciones = onVerNotificaciones)
            SeccionSostenibilidadODS()
        }
    }
}

@Preview(name = "Perfil del cliente", showBackground = true, widthDp = 360, heightDp = 1000)
@Preview(name = "Perfil con texto grande", widthDp = 320, heightDp = 1000, fontScale = 1.5f)
@Composable
private fun PerfilContenidoPreview() {
    RescatAppTheme {
        PerfilContenido(
            estado = PerfilUiState(
                nombre = "Alejandra Ramírez",
                correo = "alejandra.ramirez@ejemplo.com"
            ),
            onVerNotificaciones = {},
            onIrALogin = {},
            onIrARegistro = {},
            onCerrarSesion = {}
        )
    }
}
