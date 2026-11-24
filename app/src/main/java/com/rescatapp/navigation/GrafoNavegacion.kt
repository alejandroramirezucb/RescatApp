package com.rescatapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.auth.domain.model.SesionUsuario
import com.rescatapp.features.auth.ui.login.LoginScreen
import com.rescatapp.features.auth.ui.registro.RegistroUsuarioScreen
import com.rescatapp.features.detalle.ui.DetalleScreen
import com.rescatapp.features.explorar.ui.ExplorarScreen
import com.rescatapp.features.inicio.ui.InicioNegocioScreen
import com.rescatapp.features.inicio.ui.InicioScreen
import com.rescatapp.features.inicio.ui.OfertasNegocioScreen
import com.rescatapp.features.notificaciones.ui.NotificacionesScreen
import com.rescatapp.features.pedidos.ui.PedidosScreen
import com.rescatapp.features.perfil.ui.PerfilScreen
import com.rescatapp.features.registro.ui.RegistroScreen

fun NavGraphBuilder.grafoRescatApp(navController: NavHostController, sesion: () -> SesionUsuario?) {
    val verDetalle = { ofertaId: Int -> navController.navigate(Rutas.detalle(ofertaId)) }
    val verNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) }
    val volver: () -> Unit = { navController.popBackStack() }
    val pedirLogin: @Composable () -> Unit = {
        LoginScreen(
            onLoginExitoso = volver,
            onIrARegistro = { navController.navigate(Rutas.REGISTRO_USUARIO) },
            onVolver = volver
        )
    }

    composable(Rutas.INICIO) {
        if (sesion()?.rol == RolUsuario.NEGOCIO) {
            InicioNegocioScreen(onVerNotificaciones = verNotificaciones)
        } else {
            InicioScreen(
                onExplorar = { navController.navigate(Rutas.explorar(it)) },
                onVerDetalle = verDetalle,
                onVerNotificaciones = verNotificaciones,
                onVerComercio = { navController.navigate(Rutas.explorarComercio(it)) },
                onVerNegocios = { navController.navigate(Rutas.explorarNegocios()) }
            )
        }
    }
    composable(
        route = Rutas.EXPLORAR_POR_CATEGORIA,
        arguments = listOf(
            navArgument(ArgumentosRuta.CATEGORIA) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument(ArgumentosRuta.COMERCIO) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument(ArgumentosRuta.TIPO) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
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
        if (sesion()?.rol == RolUsuario.NEGOCIO) {
            RegistroScreen(onVolver = volver, onPublicado = volver)
        } else {
            pedirLogin()
        }
    }
    composable(Rutas.PEDIDOS) {
        if (sesion() != null) PedidosScreen() else pedirLogin()
    }
    composable(Rutas.PERFIL) {
        PerfilScreen(
            onVerNotificaciones = verNotificaciones,
            onIrALogin = { navController.navigate(Rutas.LOGIN) },
            onIrARegistro = { navController.navigate(Rutas.REGISTRO_USUARIO) }
        )
    }
    composable(Rutas.OFERTAS_NEGOCIO) {
        if (sesion()?.rol == RolUsuario.NEGOCIO) OfertasNegocioScreen() else pedirLogin()
    }
    composable(Rutas.NOTIFICACIONES) {
        NotificacionesScreen(
            onVolver = volver,
            onVerPedidos = { navController.navigate(Rutas.PEDIDOS) }
        )
    }
    composable(Rutas.LOGIN) {
        LoginScreen(
            onLoginExitoso = volver,
            onIrARegistro = {
                navController.navigate(Rutas.REGISTRO_USUARIO) {
                    popUpTo(Rutas.LOGIN) { inclusive = true }
                }
            },
            onVolver = volver
        )
    }
    composable(Rutas.REGISTRO_USUARIO) {
        RegistroUsuarioScreen(
            onRegistroExitoso = volver,
            onIrALogin = {
                navController.navigate(Rutas.LOGIN) {
                    popUpTo(Rutas.REGISTRO_USUARIO) { inclusive = true }
                }
            },
            onVolver = volver
        )
    }
}
