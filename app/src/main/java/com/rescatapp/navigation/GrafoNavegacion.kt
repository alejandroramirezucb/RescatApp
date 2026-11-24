package com.rescatapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.auth.domain.model.SesionUsuario
import com.rescatapp.features.auth.ui.AccesoRequeridoScreen
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

internal data class PantallasGrafo(
    val inicioCliente: @Composable (
        onExplorar: (Categoria?) -> Unit,
        onVerDetalle: (Int) -> Unit,
        onVerNotificaciones: () -> Unit,
        onVerComercio: (String) -> Unit,
        onVerNegocios: () -> Unit
    ) -> Unit = { onExplorar, onVerDetalle, onVerNotificaciones, onVerComercio, onVerNegocios ->
        InicioScreen(
            onExplorar = onExplorar,
            onVerDetalle = onVerDetalle,
            onVerNotificaciones = onVerNotificaciones,
            onVerComercio = onVerComercio,
            onVerNegocios = onVerNegocios
        )
    },
    val inicioNegocio: @Composable (onVerNotificaciones: () -> Unit) -> Unit = {
        InicioNegocioScreen(onVerNotificaciones = it)
    },
    val pedidos: @Composable () -> Unit = { PedidosScreen() },
    val registroOferta: @Composable (onVolver: () -> Unit, onPublicado: () -> Unit) -> Unit =
        { onVolver, onPublicado ->
            RegistroScreen(onVolver = onVolver, onPublicado = onPublicado)
        },
    val ofertasNegocio: @Composable () -> Unit = { OfertasNegocioScreen() },
    val login: @Composable (
        onLoginExitoso: () -> Unit,
        onIrARegistro: () -> Unit,
        onVolver: () -> Unit
    ) -> Unit = { onLoginExitoso, onIrARegistro, onVolver ->
        LoginScreen(
            onLoginExitoso = onLoginExitoso,
            onIrARegistro = onIrARegistro,
            onVolver = onVolver
        )
    },
    val registroUsuario: @Composable (
        onRegistroExitoso: () -> Unit,
        onIrALogin: () -> Unit,
        onVolver: () -> Unit
    ) -> Unit = { onRegistroExitoso, onIrALogin, onVolver ->
        RegistroUsuarioScreen(
            onRegistroExitoso = onRegistroExitoso,
            onIrALogin = onIrALogin,
            onVolver = onVolver
        )
    }
)

fun NavGraphBuilder.grafoRescatApp(navController: NavHostController, sesion: () -> SesionUsuario?) {
    grafoRescatApp(navController = navController, sesion = sesion, pantallas = PantallasGrafo())
}

internal fun NavGraphBuilder.grafoRescatApp(
    navController: NavHostController,
    sesion: () -> SesionUsuario?,
    pantallas: PantallasGrafo
) {
    val verDetalle = { ofertaId: Int -> navController.navigate(Rutas.detalle(ofertaId)) }
    val verNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) }
    val volver: () -> Unit = { navController.popBackStack() }
    val pedirAcceso: @Composable (Boolean) -> Unit = { requiereNegocio ->
        AccesoRequeridoScreen(
            onIrALogin = { navController.navigate(Rutas.LOGIN) },
            onIrARegistro = { navController.navigate(Rutas.REGISTRO_USUARIO) },
            onVolver = volver,
            requiereNegocio = requiereNegocio
        )
    }

    composable(Rutas.INICIO) {
        if (sesion()?.rol == RolUsuario.NEGOCIO) {
            pantallas.inicioNegocio(verNotificaciones)
        } else {
            pantallas.inicioCliente(
                { navController.navigate(Rutas.explorar(it)) },
                verDetalle,
                verNotificaciones,
                { navController.navigate(Rutas.explorarComercio(it)) },
                { navController.navigate(Rutas.explorarNegocios()) }
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
            pantallas.registroOferta(volver, volver)
        } else {
            pedirAcceso(true)
        }
    }
    composable(Rutas.PEDIDOS) {
        if (sesion() != null) pantallas.pedidos() else pedirAcceso(false)
    }
    composable(Rutas.PERFIL) {
        PerfilScreen(
            onVerNotificaciones = verNotificaciones,
            onIrALogin = { navController.navigate(Rutas.LOGIN) },
            onIrARegistro = { navController.navigate(Rutas.REGISTRO_USUARIO) }
        )
    }
    composable(Rutas.OFERTAS_NEGOCIO) {
        if (sesion()?.rol == RolUsuario.NEGOCIO) pantallas.ofertasNegocio() else pedirAcceso(true)
    }
    composable(Rutas.NOTIFICACIONES) {
        NotificacionesScreen(
            onVolver = volver,
            onVerPedidos = { navController.navigate(Rutas.PEDIDOS) }
        )
    }
    composable(Rutas.LOGIN) {
        pantallas.login(
            volver,
            {
                navController.navigate(Rutas.REGISTRO_USUARIO) {
                    popUpTo(Rutas.LOGIN) { inclusive = true }
                }
            },
            volver
        )
    }
    composable(Rutas.REGISTRO_USUARIO) {
        pantallas.registroUsuario(
            volver,
            {
                navController.navigate(Rutas.LOGIN) {
                    popUpTo(Rutas.REGISTRO_USUARIO) { inclusive = true }
                }
            },
            volver
        )
    }
}
