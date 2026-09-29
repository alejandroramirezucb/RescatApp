package com.rescatapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rescatapp.core.data.repository.RepositorioOfertas
import com.rescatapp.core.data.repository.RepositorioPedidos
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import com.rescatapp.features.detalle.ui.DetalleScreen
import com.rescatapp.features.explorar.ui.ExplorarScreen
import com.rescatapp.features.explorar.ui.ExplorarViewModel
import com.rescatapp.features.inicio.ui.InicioScreen
import com.rescatapp.features.registro.domain.RegistrarOfertaUseCase
import com.rescatapp.features.registro.ui.RegistroScreen

@Composable
fun RescatNavHost(ofertas: RepositorioOfertas, pedidos: RepositorioPedidos) {
    val navController = rememberNavController()
    val reservar = remember(ofertas, pedidos) { ReservarOfertaUseCase(ofertas, pedidos) }
    val publicar = remember(ofertas) { RegistrarOfertaUseCase(ofertas) }

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
                onPublicar = { navController.navigate("registro") }
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
        ) { entrada ->
            val id = entrada.arguments?.getInt("ofertaId") ?: -1
            val lista by ofertas.ofertas.collectAsStateWithLifecycle()
            DetalleScreen(
                oferta = lista.find { it.id == id },
                onVolver = { navController.popBackStack() },
                onReservar = { reservar(id) != null }
            )
        }
        composable("registro") {
            RegistroScreen(
                onVolver = { navController.popBackStack() },
                onPublicar = { campos ->
                    val guardada = publicar(campos) != null
                    if (guardada) navController.popBackStack()
                    guardada
                }
            )
        }
    }
}
