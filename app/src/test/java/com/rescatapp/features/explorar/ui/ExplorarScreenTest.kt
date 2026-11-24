package com.rescatapp.features.explorar.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.agruparComercios
import com.rescatapp.features.explorar.domain.FiltrosOfertas
import com.rescatapp.features.explorar.domain.TipoBusquedaExplorar
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
class ExplorarScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val comercios = ofertasDeEjemplo.agruparComercios()

    @Test
    fun permiteCambiarAlFiltroDeNegociosYSeleccionarUnComercio() {
        var tipoSeleccionado: TipoBusquedaExplorar? = null
        var comercioSeleccionado: String? = null

        compose.setContent {
            RescatAppTheme {
                ExplorarContenido(
                    estado = ExplorarUiState(
                        filtros = FiltrosOfertas(tipo = TipoBusquedaExplorar.NEGOCIOS),
                        ofertas = ofertasDeEjemplo,
                        comercios = comercios
                    ),
                    onBuscar = {},
                    onCategoriaSeleccionada = {},
                    onOrdenSeleccionado = {},
                    onLimpiarFiltros = {},
                    onVerDetalle = {},
                    onTipoSeleccionado = { tipoSeleccionado = it },
                    onComercioSeleccionado = { comercioSeleccionado = it }
                )
            }
        }

        compose.onNodeWithText("Negocios").performClick()
        assertEquals(TipoBusquedaExplorar.NEGOCIOS, tipoSeleccionado)

        compose.onNodeWithText("Café Aroma").assertIsDisplayed().performClick()
        assertEquals("Café Aroma", comercioSeleccionado)
    }

    @Test
    fun sinCoincidenciasMuestraEstadoVacioClaroParaProductosYNegocios() {
        compose.setContent {
            RescatAppTheme {
                ExplorarContenido(
                    estado = ExplorarUiState(
                        filtros = FiltrosOfertas(texto = "NoExiste"),
                        ofertas = emptyList(),
                        comercios = emptyList()
                    ),
                    onBuscar = {},
                    onCategoriaSeleccionada = {},
                    onOrdenSeleccionado = {},
                    onLimpiarFiltros = {},
                    onVerDetalle = {}
                )
            }
        }

        compose.onNodeWithText("No encontramos productos ni negocios con esos filtros")
            .assertIsDisplayed()
        compose.onNodeWithText("Ver todo el catálogo").assertIsDisplayed()
    }
}
