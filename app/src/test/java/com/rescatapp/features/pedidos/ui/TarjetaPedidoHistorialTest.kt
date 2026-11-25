package com.rescatapp.features.pedidos.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.EstadoPagoSimulado
import com.rescatapp.core.model.EstadoPedido
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class, qualifiers = "w393dp-h852dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class TarjetaPedidoHistorialTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun historialMuestraLaAnulacionDelPagoSimulado() {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoHistorial(
                    pedidoDePrueba.copy(
                        estado = EstadoPedido.CANCELADO,
                        estadoPagoSimulado = EstadoPagoSimulado.ANULADO
                    )
                )
            }
        }
        compose.onNodeWithText("Pago simulado anulado").assertIsDisplayed()
    }

    @Test
    fun historialDelNegocioMuestraElComprador() {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoHistorial(
                    pedidoDePrueba.copy(estado = EstadoPedido.RECOGIDO),
                    esNegocio = true
                )
            }
        }

        compose.onNodeWithText("Ana Pérez").assertIsDisplayed()
        compose.onNodeWithText("Teléfono: 71234567").assertIsDisplayed()
    }

    @Test
    fun historialDelClienteNoMuestraDetallesDelComprador() {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoHistorial(pedidoDePrueba.copy(estado = EstadoPedido.RECOGIDO))
            }
        }

        compose.onNodeWithText("Cliente comprador").assertDoesNotExist()
        compose.onNodeWithText("Ana Pérez").assertDoesNotExist()
    }
}
