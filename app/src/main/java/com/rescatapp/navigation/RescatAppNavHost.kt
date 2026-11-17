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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.core.notification.SolicitudPermisoNotificaciones
import com.rescatapp.features.auth.ui.SupabaseAuthViewModel
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

@Composable
fun RescatAppNavHost() {
    SolicitudPermisoNotificaciones()
    val auth: SupabaseAuthViewModel = hiltViewModel()
    val sesion by auth.sesion.collectAsStateWithLifecycle()
    val listo by auth.inicializado.collectAsStateWithLifecycle()
    val errorConexion by auth.errorSincronizacion.collectAsStateWithLifecycle()
    val aviso by auth.aviso.collectAsStateWithLifecycle()
    if (!listo) {
        androidx.compose.material3.CircularProgressIndicator()
        return
    }
    com.rescatapp.features.auth.ui.DialogoNuevaContrasena(auth)
    aviso?.let { mensaje ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = auth::limpiarAviso,
            title = { Text("Acceso a tu cuenta") },
            text = { Text(mensaje) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = auth::limpiarAviso) {
                    Text("Entendido")
                }
            }
        )
    }

    val navController = rememberNavController()
    var cuentaAnterior by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(sesion?.usuarioId)
    }
    androidx.compose.runtime.LaunchedEffect(sesion?.usuarioId) {
        if (cuentaAnterior != sesion?.usuarioId) {
            cuentaAnterior = sesion?.usuarioId
            navController.navigate(Rutas.INICIO) {
                popUpTo(navController.graph.id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    val entradaActual by navController.currentBackStackEntryAsState()
    val destinoPrincipal = DestinoPrincipal.desdeRuta(entradaActual?.destination?.route)
    val verDetalle = { ofertaId: Int -> navController.navigate(Rutas.detalle(ofertaId)) }
    val verNotificaciones = { navController.navigate(Rutas.NOTIFICACIONES) }
    val verLogin = { navController.navigate(Rutas.LOGIN) }
    val verRegistroUsuario = { navController.navigate(Rutas.REGISTRO_USUARIO) }
    val volver: () -> Unit = { navController.popBackStack() }
    val rol by UsuarioDemo.rol.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            errorConexion?.let { mensaje ->
                androidx.compose.foundation.layout.Column {
                    Text(
                        mensaje,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                    )
                    androidx.compose.material3.TextButton(onClick = auth::sincronizar) {
                        Text("Reintentar")
                    }
                }
            }
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
            composable(Rutas.INICIO) {
                if (rol == RolUsuario.NEGOCIO) {
                    InicioNegocioScreen(onVerNotificaciones = verNotificaciones)
                } else {
                    InicioScreen(
                        onExplorar = { navController.navigate(Rutas.explorar(it)) },
                        onVerDetalle = verDetalle,
                        onVerNotificaciones = verNotificaciones
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
                if (sesion?.rol ==
                    RolUsuario.NEGOCIO
                ) {
                    RegistroScreen(onVolver = volver, onPublicado = volver)
                } else {
                    LoginScreen(
                        onLoginExitoso = volver,
                        onIrARegistro = verRegistroUsuario,
                        onVolver = volver
                    )
                }
            }
            composable(Rutas.PEDIDOS) {
                if (sesion != null) {
                    PedidosScreen()
                } else {
                    LoginScreen(
                        onLoginExitoso = volver,
                        onIrARegistro = verRegistroUsuario,
                        onVolver = volver
                    )
                }
            }
            composable(Rutas.PERFIL) {
                PerfilScreen(
                    onVerNotificaciones = verNotificaciones,
                    onIrALogin = verLogin,
                    onIrARegistro = verRegistroUsuario
                )
            }
            composable(Rutas.OFERTAS_NEGOCIO) {
                if (sesion?.rol == RolUsuario.NEGOCIO) {
                    OfertasNegocioScreen()
                } else {
                    LoginScreen(
                        onLoginExitoso = volver,
                        onIrARegistro = verRegistroUsuario,
                        onVolver = volver
                    )
                }
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
                    onVolver = volver,
                    onRecuperar = { navController.navigate("recuperar_acceso") }
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
            composable("recuperar_acceso") {
                com.rescatapp.features.auth.ui.RecuperarAccesoScreen(onVolver = volver)
            }
        }
    }
}
