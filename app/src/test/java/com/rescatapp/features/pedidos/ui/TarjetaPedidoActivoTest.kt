package com.rescatapp.features.pedidos.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPagoSimulado
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
        codigoServidor = "#APV-77",
        clienteNombre = "Ana Pérez",
        clienteTelefono = "71234567"
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
                    pedidoConProgreso.copy(
                        pedido = pedido.copy(estado = estado)
                    ),
                    false,
                    {
                    },
                    {}
                )
            }
        }
        compose.onNodeWithText("Cancelar reserva").assertDoesNotExist()
    }

    @Test
    fun dialogoExplicaLaAnulacionSinCobroRealYExigeConfirmar() {
        var confirmaciones = 0
        compose.setContent {
            RescatAppTheme {
                DialogoCancelarPedido(pedido, { confirmaciones++ }, {})
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
    fun historialMuestraLaAnulacionDelPagoSimulado() {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoHistorial(
                    pedido.copy(
                        estado = EstadoPedido.CANCELADO,
                        estadoPagoSimulado = EstadoPagoSimulado.ANULADO
                    )
                )
            }
        }
        compose.onNodeWithText("Pago simulado anulado").assertIsDisplayed()
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
                        pedido = pedido.copy(clienteNombre = "  ", clienteTelefono = null)
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

    @Test
    fun historialDelNegocioMuestraElComprador() {
        compose.setContent {
            RescatAppTheme {
                TarjetaPedidoHistorial(
                    pedido.copy(estado = EstadoPedido.RECOGIDO),
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
                TarjetaPedidoHistorial(pedido.copy(estado = EstadoPedido.RECOGIDO))
            }
        }

        compose.onNodeWithText("Cliente comprador").assertDoesNotExist()
        compose.onNodeWithText("Ana Pérez").assertDoesNotExist()
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
