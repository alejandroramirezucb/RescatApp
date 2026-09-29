package com.rescatapp.features.registro.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rescatapp.core.designsystem.components.BarraSuperior
import com.rescatapp.core.designsystem.components.BotonPrincipal

@Composable
fun RegistroScreen(
    onVolver: () -> Unit,
    onPublicado: () -> Unit,
    viewModel: RegistroViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(estado.guardadoExitoso) {
        if (estado.guardadoExitoso) onPublicado()
    }

    Scaffold(topBar = {
        BarraSuperior(titulo = "Publicar oferta", onVolver = onVolver)
    }) { espacioInterno ->
        Column(
            modifier = Modifier
                .padding(espacioInterno)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FormularioOferta(
                campos = estado.campos,
                errores = estado.errores,
                onCampoCambiado = viewModel::onCampoCambiado
            )
            BotonPrincipal(texto = "Publicar oferta", onClick = viewModel::publicar)
            estado.mensaje?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
