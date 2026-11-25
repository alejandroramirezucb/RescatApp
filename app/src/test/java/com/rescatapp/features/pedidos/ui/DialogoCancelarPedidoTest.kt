package com.rescatapp.features.pedidos.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Pedido
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
class DialogoCancelarPedidoTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun dialogoExplicaLaAnulacionSinCobroRealYExigeConfirmar() {
        var confirmaciones = 0
        compose.setContent {
            RescatAppTheme {
                DialogoCancelarPedido(pedidoDePrueba, { confirmaciones++ }, {})
            }
        }
        compose.onNodeWithText(
            "El pago simulado se anulará al confirmar la cancelación.",
            substring = true
        ).assertExists()
        compose.onNodeWithText(
            "no se necesita un reembolso bancario.",
            substring = true
        ).assertExists()
        assertEquals(0, confirmaciones)
        compose.onNodeWithText("Sí, cancelar").performClick()
        assertEquals(1, confirmaciones)
    }

    @Test
    fun dialogoCancelarPedidoOcultaCodigoAlNegocio() {
        compose.setContent {
            RescatAppTheme {
                DialogoCancelarPedido(
                    pedido = pedidoDePrueba,
                    onConfirmar = {},
                    onDescartar = {},
                    esNegocio = true
                )
            }
        }

        compose.onNodeWithText("Pedido #77").assertIsDisplayed()
        compose.onNodeWithText("#APV-77").assertDoesNotExist()
    }

    @Test
    fun dialogoCancelarPedidoMuestraCodigoAlCliente() {
        compose.setContent {
            RescatAppTheme {
                DialogoCancelarPedido(
                    pedido = pedidoDePrueba,
                    onConfirmar = {},
                    onDescartar = {},
                    esNegocio = false
                )
            }
        }

        compose.onNodeWithText("#APV-77").assertIsDisplayed()
    }
}
