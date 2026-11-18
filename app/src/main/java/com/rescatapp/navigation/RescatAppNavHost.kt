package com.rescatapp.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.notification.SolicitudPermisoNotificaciones
import kotlinx.coroutines.delay

@Composable
fun RescatAppNavHost(viewModel: PrincipalViewModel = hiltViewModel()) {
    SolicitudPermisoNotificaciones()
    val sesion by viewModel.sesion.collectAsStateWithLifecycle()
    val errorConexion by viewModel.errorConexion.collectAsStateWithLifecycle()
    val rol by UsuarioDemo.rol.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val cicloDeVida = LocalLifecycleOwner.current

    LaunchedEffect(sesion?.usuarioId) {
        cicloDeVida.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                viewModel.sincronizar()
                delay(INTERVALO_SINCRONIZACION)
            }
        }
    }

    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoPrincipal = DestinoPrincipal.desdeRuta(entradaActual?.destination?.route)

    Scaffold(
        topBar = {
            errorConexion?.let { AvisoSinConexion(it, viewModel::reintentar) }
        },
        bottomBar = {
            destinoPrincipal?.let {
                BarraNavegacionInferior(it, rol, navController::navegarAPestana)
            }
        },
        floatingActionButton = {
            if (destinoPrincipal?.muestraBotonPublicar(rol) == true) {
                ExtendedFloatingActionButton(onClick = { navController.navigate(Rutas.REGISTRO) }) {
                    Icon(Icons.Outlined.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Publicar oferta")
                }
            }
        }
    ) { espacioInterno ->
        NavHost(
            navController = navController,
            startDestination = Rutas.INICIO,
            modifier = Modifier.padding(espacioInterno).consumeWindowInsets(espacioInterno)
        ) {
            grafoRescatApp(navController, sesion, rol)
        }
    }
}

@Composable
private fun AvisoSinConexion(mensaje: String, onReintentar: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.errorContainer) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onReintentar) { Text("Reintentar") }
        }
    }
}

private const val INTERVALO_SINCRONIZACION = 10_000L
