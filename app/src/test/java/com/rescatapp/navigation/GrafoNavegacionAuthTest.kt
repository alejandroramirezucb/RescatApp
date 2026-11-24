package com.rescatapp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.SesionUsuario
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class, qualifiers = "w393dp-h852dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class GrafoNavegacionAuthTest {
    @get:Rule
    val compose = createComposeRule()

    private val sesionCliente = SesionUsuario(
        usuarioId = "u-1",
        nombre = "Ana",
        correo = "ana@correo.com",
        rol = RolUsuario.CLIENTE
    )

    @Test
    fun usuarioSinSesionEnPedidosVeAccesoSeparadoYRegresaTrasLogin() {
        var sesion by mutableStateOf<SesionUsuario?>(null)
        lateinit var controlador: NavHostController

        compose.setContent {
            controlador = rememberNavController()
            RescatAppTheme {
                NavHost(navController = controlador, startDestination = Rutas.INICIO) {
                    grafoRescatApp(
                        navController = controlador,
                        sesion = { sesion },
                        pantallas = pantallasPrueba(
                            onAutenticarCliente = { sesion = sesionCliente }
                        )
                    )
                }
            }
        }

        compose.runOnUiThread { controlador.navigate(Rutas.PEDIDOS) }

        compose.onAllNodesWithText("Accede a tu cuenta").onFirst().assertIsDisplayed()
        compose.onNodeWithText("Iniciar sesión").assertIsDisplayed()
        compose.onNodeWithText("Registrarse").assertIsDisplayed()
        compose.onNodeWithText("Formulario Login").assertDoesNotExist()

        compose.onNodeWithText("Iniciar sesión").performClick()
        assertEquals(Rutas.LOGIN, controlador.currentDestination?.route)

        compose.onNodeWithText("Completar Login").performClick()
        assertEquals(Rutas.PEDIDOS, controlador.currentDestination?.route)
        compose.onNodeWithText("Pantalla Pedidos").assertIsDisplayed()
    }

    @Test
    fun usuarioSinSesionEligeRegistrarseYNoQuedaAtrapadoEnLogin() {
        var sesion by mutableStateOf<SesionUsuario?>(null)
        lateinit var controlador: NavHostController

        compose.setContent {
            controlador = rememberNavController()
            RescatAppTheme {
                NavHost(navController = controlador, startDestination = Rutas.INICIO) {
                    grafoRescatApp(
                        navController = controlador,
                        sesion = { sesion },
                        pantallas = pantallasPrueba(
                            onAutenticarCliente = { sesion = sesionCliente }
                        )
                    )
                }
            }
        }

        compose.runOnUiThread { controlador.navigate(Rutas.PEDIDOS) }
        compose.onNodeWithText("Registrarse").performClick()
        assertEquals(Rutas.REGISTRO_USUARIO, controlador.currentDestination?.route)

        compose.onNodeWithText("Completar Registro").performClick()
        assertEquals(Rutas.PEDIDOS, controlador.currentDestination?.route)
        assertEquals(Rutas.INICIO, controlador.previousBackStackEntry?.destination?.route)
        compose.onNodeWithText("Pantalla Pedidos").assertIsDisplayed()
    }

    @Test
    fun cambiarDeLoginARegistroSacaLoginDelBackStack() {
        var sesion by mutableStateOf<SesionUsuario?>(null)
        lateinit var controlador: NavHostController

        compose.setContent {
            controlador = rememberNavController()
            RescatAppTheme {
                NavHost(navController = controlador, startDestination = Rutas.INICIO) {
                    grafoRescatApp(
                        navController = controlador,
                        sesion = { sesion },
                        pantallas = pantallasPrueba(
                            onAutenticarCliente = { sesion = sesionCliente }
                        )
                    )
                }
            }
        }

        compose.runOnUiThread { controlador.navigate(Rutas.PEDIDOS) }
        compose.onNodeWithText("Iniciar sesión").performClick()
        assertEquals(Rutas.LOGIN, controlador.currentDestination?.route)

        compose.onNodeWithText("Ir a registro").performClick()
        assertEquals(Rutas.REGISTRO_USUARIO, controlador.currentDestination?.route)

        compose.onNodeWithText("Completar Registro").performClick()
        assertEquals(Rutas.PEDIDOS, controlador.currentDestination?.route)
        assertEquals(Rutas.INICIO, controlador.previousBackStackEntry?.destination?.route)
    }

    @Test
    fun rutasDeNegocioSinRolNegocioMuestranAccesoParaComercios() {
        val sesion by mutableStateOf<SesionUsuario?>(sesionCliente)
        lateinit var controlador: NavHostController

        compose.setContent {
            controlador = rememberNavController()
            RescatAppTheme {
                NavHost(navController = controlador, startDestination = Rutas.INICIO) {
                    grafoRescatApp(
                        navController = controlador,
                        sesion = { sesion },
                        pantallas = pantallasPrueba(onAutenticarCliente = {})
                    )
                }
            }
        }

        compose.runOnUiThread { controlador.navigate(Rutas.REGISTRO) }
        compose.onAllNodesWithText("Acceso para comercios").onFirst().assertIsDisplayed()
        compose.onNodeWithText("Iniciar sesión").assertIsDisplayed()
        compose.onNodeWithText("Registrarse").assertIsDisplayed()

        compose.runOnUiThread { controlador.navigate(Rutas.OFERTAS_NEGOCIO) }
        compose.onAllNodesWithText("Acceso para comercios").onFirst().assertIsDisplayed()
    }

    private fun pantallasPrueba(onAutenticarCliente: () -> Unit) = PantallasGrafo(
        inicioCliente = { _, _, _ -> Text("Pantalla Inicio") },
        inicioNegocio = { Text("Pantalla Inicio Negocio") },
        pedidos = { Text("Pantalla Pedidos") },
        registroOferta = { _, _ -> Text("Pantalla Crear Oferta") },
        ofertasNegocio = { Text("Pantalla Ofertas Negocio") },
        login = { onLoginExitoso, onIrARegistro, _ ->
            Column {
                Text("Formulario Login")
                Button(onClick = {
                    onAutenticarCliente()
                    onLoginExitoso()
                }) { Text("Completar Login") }
                Button(onClick = onIrARegistro) { Text("Ir a registro") }
            }
        },
        registroUsuario = { onRegistroExitoso, onIrALogin, _ ->
            Column {
                Text("Formulario Registro")
                Button(onClick = {
                    onAutenticarCliente()
                    onRegistroExitoso()
                }) { Text("Completar Registro") }
                Button(onClick = onIrALogin) { Text("Ir a login") }
            }
        }
    )
}
