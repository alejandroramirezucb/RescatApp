package com.rescatapp.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.detalle.ui.DetalleScreen
import com.rescatapp.features.explorar.ui.ExplorarScreen
import com.rescatapp.features.inicio.ui.InicioNegocioScreen
import com.rescatapp.features.inicio.ui.InicioScreen
import com.rescatapp.features.pedidos.ui.PedidosScreen
import com.rescatapp.features.perfil.ui.PerfilScreen
import com.rescatapp.features.registro.ui.RegistroScreen

@Composable
fun RescatAppNavHost() {
    val navController = rememberNavController()
    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoPrincipal = DestinoPrincipal.desdeRuta(entradaActual?.destination?.route)
    val verDetalle = { ofertaId: Int -> navController.navigate(Rutas.detalle(ofertaId)) }
    val volver: () -> Unit = { navController.popBackStack() }
    val rol by UsuarioDemo.rol.collectAsStateWithLifecycle()

    Scaffold(
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
            composable(Rutas.INICIO) {
                if (rol == com.rescatapp.core.model.RolUsuario.NEGOCIO) {
                    InicioNegocioScreen()
                } else {
                    InicioScreen(
                        onExplorar = { navController.navigate(Rutas.explorar(it)) },
                        onVerDetalle = verDetalle
                    )
                }
            }
            composable(
                route = Rutas.EXPLORAR_POR_CATEGORIA,
                arguments = listOf(
                    navArgument(ArgumentosRuta.CATEGORIA) {
                        type = NavType.StringType
                        nullable = true
                    }
                )
            ) {
                ExplorarScreen(onVerDetalle = verDetalle)
            }
            composable(
                route = Rutas.DETALLE,
                arguments = listOf(navArgument(ArgumentosRuta.OFERTA_ID) { type = NavType.IntType })
            ) {
                DetalleScreen(onVolver = volver)
            }
            composable(Rutas.REGISTRO) {
                RegistroScreen(onVolver = volver, onPublicado = volver)
            }
            composable(Rutas.PEDIDOS) { PedidosScreen() }
            composable(Rutas.PERFIL) { PerfilScreen() }
        }
    }
}

private fun NavController.navegarAPestana(destino: DestinoPrincipal) {
    if (destino == DestinoPrincipal.INICIO) {
        popBackStack(Rutas.INICIO, inclusive = false, saveState = true)
        return
    }
    navigate(destino.ruta) {
        popUpTo(Rutas.INICIO) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
