package com.rescatapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.detalle.ui.DetalleScreen
import com.rescatapp.features.explorar.ui.ExplorarScreen
import com.rescatapp.features.explorar.ui.ExplorarViewModel
import com.rescatapp.features.inicio.ui.InicioScreen
import com.rescatapp.features.pedidos.ui.PedidosScreen
import com.rescatapp.features.perfil.ui.PerfilScreen
import com.rescatapp.features.registro.ui.RegistroScreen

@Composable
fun RescatNavHost() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val destinoActual = DestinoPrincipal.entries.find { it.rutaPatron == rutaActual }

    Scaffold(
        bottomBar = {
            if (destinoActual != null) {
                BarraNavegacionInferior(destinoActual, navController::navegarPrincipal)
            }
        },
        floatingActionButton = {
            if (destinoActual == DestinoPrincipal.INICIO ||
                destinoActual == DestinoPrincipal.EXPLORAR
            ) {
                ExtendedFloatingActionButton(
                    onClick = { navController.navigate("registro") }
                ) { Text("Publicar oferta") }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = DestinoPrincipal.INICIO.ruta,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(
                route = "explorar?categoria={categoria}",
                arguments = listOf(
                    navArgument("categoria") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) {
                val viewModel: ExplorarViewModel = hiltViewModel()
                val estado by viewModel.uiState.collectAsStateWithLifecycle()

                ExplorarScreen(
                    estado = estado,
                    onBuscar = viewModel::buscar,
                    onCategoria = viewModel::seleccionarCategoria,
                    onOrden = viewModel::seleccionarOrden,
                    onVerDetalle = { id -> navController.navigate("detalle/$id") }
                )
            }
            composable("inicio") {
                InicioScreen { categoria: Categoria? ->
                    val ruta = categoria?.let { "explorar?categoria=${it.name}" } ?: "explorar"
                    navController.navigate(ruta)
                }
            }
            composable(
                route = "detalle/{ofertaId}",
                arguments = listOf(navArgument("ofertaId") { type = NavType.IntType })
            ) {
                DetalleScreen(onVolver = { navController.popBackStack() })
            }
            composable("registro") {
                RegistroScreen(onVolver = { navController.popBackStack() })
            }
            composable(DestinoPrincipal.PEDIDOS.ruta) {
                PedidosScreen()
            }
            composable(DestinoPrincipal.PERFIL.ruta) {
                PerfilScreen()
            }
        }
    }
}

private fun NavController.navegarPrincipal(destino: DestinoPrincipal) {
    navigate(destino.ruta) {
        launchSingleTop = true
        restoreState = true
        popUpTo(DestinoPrincipal.INICIO.ruta) {
            saveState = true
        }
    }
}
