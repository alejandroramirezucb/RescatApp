package com.rescatapp.features.auth.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rescatapp.core.designsystem.theme.RescatAppTheme
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
class AccesoRequeridoScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun muestraOpcionesSeparadasDeIniciarSesionYRegistrarseParaInvitado() {
        compose.setContent {
            RescatAppTheme {
                AccesoRequeridoScreen(
                    onIrALogin = {},
                    onIrARegistro = {},
                    onVolver = {},
                    requiereNegocio = false
                )
            }
        }

        compose.onAllNodesWithText("Accede a tu cuenta").onFirst().assertIsDisplayed()
        compose.onNodeWithText("Elige cómo deseas continuar").assertIsDisplayed()
        compose.onNodeWithText("Iniciar sesión").assertIsDisplayed()
        compose.onNodeWithText("Registrarse").assertIsDisplayed()
    }

    @Test
    fun muestraMensajeDeComercioCuandoRequiereNegocio() {
        compose.setContent {
            RescatAppTheme {
                AccesoRequeridoScreen(
                    onIrALogin = {},
                    onIrARegistro = {},
                    onVolver = {},
                    requiereNegocio = true
                )
            }
        }

        compose.onAllNodesWithText("Acceso para comercios").onFirst().assertIsDisplayed()
        compose.onNodeWithText("Iniciar sesión").assertIsDisplayed()
        compose.onNodeWithText("Registrarse").assertIsDisplayed()
    }

    @Test
    fun botonesDeAccesoDisparanSusCallbacksDeFormaIndependiente() {
        var logins = 0
        var registros = 0
        var regresos = 0

        compose.setContent {
            RescatAppTheme {
                AccesoRequeridoScreen(
                    onIrALogin = { logins++ },
                    onIrARegistro = { registros++ },
                    onVolver = { regresos++ }
                )
            }
        }

        compose.onNodeWithText("Iniciar sesión").performClick()
        assertEquals(1, logins)
        assertEquals(0, registros)

        compose.onNodeWithText("Registrarse").performClick()
        assertEquals(1, logins)
        assertEquals(1, registros)

        compose.onNodeWithContentDescription("Volver").performClick()
        assertEquals(1, regresos)
    }
}
