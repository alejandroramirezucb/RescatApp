package com.rescatapp.features.registro.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BarraSuperior
import com.rescatapp.core.designsystem.components.BotonPrincipal
import com.rescatapp.core.model.UsuarioDemo

@Composable
fun RegistroScreen(
    onVolver: () -> Unit,
    onPublicado: () -> Unit,
    viewModel: RegistroViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val gestorFoco = LocalFocusManager.current

    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) onPublicado()
    }

    LaunchedEffect(Unit) {
        if (estado.campos.comercio.isBlank()) {
            viewModel.onCampoCambiado(
                estado.campos.copy(
                    comercio = UsuarioDemo.NEGOCIO_DEMO,
                    categoria = com.rescatapp.core.model.Categoria.PANADERIA,
                    pesoKg = "1,0",
                    horaRetiroDesde = "18:00",
                    horaRetiroHasta = "20:00"
                )
            )
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { BarraSuperior(titulo = "Crear oferta", onVolver = onVolver) }
    ) { espacioInterno ->
        Box(
            modifier = Modifier.fillMaxSize().padding(espacioInterno).imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Comparte un nuevo pack", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = estado.campos.comercio.ifBlank { UsuarioDemo.NEGOCIO_DEMO },
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Completa la información para que tus clientes conozcan la oferta.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                FormularioOferta(
                    campos = estado.campos,
                    errores = estado.errores,
                    onCampoCambiado = viewModel::onCampoCambiado
                )
                estado.mensaje?.let { mensaje ->
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = mensaje,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                BotonPrincipal(
                    texto = if (estado.guardando) "Guardando..." else "Publicar oferta",
                    habilitado = !estado.guardando && !estado.guardadoExitoso,
                    onClick = {
                        gestorFoco.clearFocus()
                        viewModel.publicar()
                    }
                )
            }
        }
    }
}
