package com.rescatapp.features.inicio.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
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
class PedidosRecientesNegocioTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun pedidosRecientesNegocioNoMuestranCodigoDeRescateDelCliente() {
        val pedido = Pedido(
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
            estado = EstadoPedido.LISTO,
            codigoServidor = "#APV-77"
        )

        compose.setContent {
            RescatAppTheme {
                PedidosRecientesNegocio(pedidos = listOf(pedido))
            }
        }

        compose.onNodeWithText("Pedido #77 · Listo").assertIsDisplayed()
        compose.onNodeWithText("#APV-77", substring = true).assertDoesNotExist()
        compose.onNodeWithText("Código de rescate", substring = true).assertDoesNotExist()
    }
}
