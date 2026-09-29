package com.rescatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rescatapp.features.detalle.ui.DetalleScreen
import com.rescatapp.features.explorar.ui.ExplorarScreen
import com.rescatapp.features.explorar.ui.ExplorarViewModel
import com.rescatapp.features.inicio.ui.InicioScreen
import com.rescatapp.features.perfil.ui.PerfilScreen
import com.rescatapp.features.registro.ui.RegistroScreen

fun construirRutaExplorar(categoria: String?): String =
    if (!categoria.isNullOrBlank()) "explorar?categoria=$categoria" else "explorar"

@Composable
fun RescatNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "explorar") {
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
                onVerDetalle = { id -> navController.navigate("detalle/$id") },
                onInicio = { navController.navigate("inicio") },
                onPublicar = { navController.navigate("registro") },
                onPedidos = { navController.navigate("pedidos") },
                onPerfil = { navController.navigate("perfil") }
            )
        }
        composable("inicio") {
            InicioScreen(
                onExplorar = { categoria ->
                    navController.navigate(construirRutaExplorar(categoria))
                },
                onPublicar = { navController.navigate("registro") },
                onVerDetalle = { id -> navController.navigate("detalle/$id") }
            )
        }
        composable(
            route = "detalle/{ofertaId}",
            arguments = listOf(navArgument("ofertaId") { type = NavType.IntType })
        ) {
            DetalleScreen(onVolver = { navController.popBackStack() })
        }
        composable("registro") {
            RegistroScreen(
                onVolver = { navController.popBackStack() },
                onPublicado = { navController.popBackStack() }
            )
        }
        composable("perfil") {
            PerfilScreen(onVolver = { navController.popBackStack() })
        }
        composable("pedidos") {
            com.rescatapp.features.pedidos.ui.PedidosScreen(
                onVolver = { navController.popBackStack() },
                onInicio = { navController.navigate("inicio") },
                onExplorar = { navController.navigate("explorar") },
                onPublicar = { navController.navigate("registro") },
                onPerfil = { navController.navigate("perfil") }
            )
        }
    }
}
