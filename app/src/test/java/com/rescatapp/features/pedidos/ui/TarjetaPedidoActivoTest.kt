package com.rescatapp.features.pedidos.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.PasoProgreso
import com.rescatapp.core.model.Pedido
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

    private val pedido = Pedido(
        id = 77,
        ofertaId = 1,
        nombreOferta = "Pack Sorpresa",
        comercio = "Panadería La Central",
        categoria = Categoria.PANADERIA,
        precioPagado = 25.0,
        ahorro = 20.0,
        pesoKg = 1.0,
        horaRetiroDesde = "18:00",
        horaRetiroHasta = "20:00",
        estado = EstadoPedido.RESERVADO,
        codigoServidor = "#APV-77"
    )

    private val pedidoConProgreso = PedidoConProgreso(
        pedido = pedido,
        pasos = listOf(
            PasoProgreso("Reservado", completado = true, actual = true),
            PasoProgreso("Preparando", completado = false, actual = false),
            PasoProgreso("Listo", completado = false, actual = false)
        )
    )

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
    }

    @Test
    fun dialogoCancelarPedidoOcultaCodigoAlNegocio() {
        compose.setContent {
            RescatAppTheme {
                DialogoCancelarPedido(
                    pedido = pedido,
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
                    pedido = pedido,
                    onConfirmar = {},
                    onDescartar = {},
                    esNegocio = false
                )
            }
        }

        compose.onNodeWithText("#APV-77").assertIsDisplayed()
    }
}
