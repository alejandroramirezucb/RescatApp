package com.rescatapp.features.pedidos.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.PasoProgreso
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
class TarjetaPedidoActivoTest {
    @get:Rule
    val compose = createComposeRule()

    private val pedido = pedidoDePrueba

    private val pedidoConProgreso = PedidoConProgreso(
        pedido = pedido,
        pasos = listOf(
            PasoProgreso("Reservado", completado = true, actual = true),
            PasoProgreso("Preparando", completado = false, actual = false),
            PasoProgreso("Listo", completado = false, actual = false)
        )
    )

    @Test
    fun clientePuedeSolicitarCancelarUnaReserva() {
        var cancelado: Pedido? = null
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoActivo(pedidoConProgreso, false, {}, { cancelado = it })
            }
        }
        compose.onNodeWithText("Cancelar reserva").performClick()
        assertEquals(pedido, cancelado)
    }

    @Test
    fun clienteNoPuedeCancelarUnPedidoEnPreparacion() {
        comprobarCancelacionOculta(EstadoPedido.PREPARANDO)
    }

    @Test
    fun clienteNoPuedeCancelarUnPedidoListo() {
        comprobarCancelacionOculta(EstadoPedido.LISTO)
    }

    private fun comprobarCancelacionOculta(estado: EstadoPedido) {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoActivo(
                    pedidoConProgreso.copy(pedido = pedido.copy(estado = estado)),
                    false,
                    {},
                    {}
                )
            }
        }
        compose.onNodeWithText("Cancelar reserva").assertDoesNotExist()
    }

    @Test
    fun clienteVeSuCodigoDeRescateEnPedidoActivo() {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoActivo(
                    activo = pedidoConProgreso,
                    esNegocio = false,
                    onAvanzar = {},
                    onCancelar = {}
                )
            }
        }

        compose.onNodeWithText("CÓDIGO DE RESCATE").assertIsDisplayed()
        compose.onNodeWithText("#APV-77").assertIsDisplayed()
        compose.onNodeWithText("Cliente comprador").assertDoesNotExist()
        compose.onNodeWithText("Ana Pérez").assertDoesNotExist()
        compose.onNodeWithText("Teléfono: 71234567").assertDoesNotExist()
    }

    @Test
    fun negocioNoVeElCodigoDeRescateEnPedidoActivo() {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoActivo(
                    activo = pedidoConProgreso,
                    esNegocio = true,
                    onAvanzar = {},
                    onCancelar = {}
                )
            }
        }

        compose.onNodeWithText("CÓDIGO DE RESCATE").assertDoesNotExist()
        compose.onNodeWithText("#APV-77").assertDoesNotExist()
        compose.onNodeWithText("Comenzar a preparar").assertIsDisplayed()
        compose.onNodeWithText("Cliente comprador").assertIsDisplayed()
        compose.onNodeWithText("Ana Pérez").assertIsDisplayed()
        compose.onNodeWithText("Teléfono: 71234567").assertIsDisplayed()
    }

    @Test
    fun negocioVeUnMensajeCuandoElPedidoNoTieneDatosDelCliente() {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoActivo(
                    activo = pedidoConProgreso.copy(
                        pedido = pedido.copy(clienteNombre = null, clienteTelefono = null)
                    ),
                    esNegocio = true,
                    onAvanzar = {},
                    onCancelar = {}
                )
            }
        }

        compose.onNodeWithText("Nombre no disponible").assertIsDisplayed()
        compose.onNodeWithText("Teléfono no disponible").assertIsDisplayed()
    }
}
