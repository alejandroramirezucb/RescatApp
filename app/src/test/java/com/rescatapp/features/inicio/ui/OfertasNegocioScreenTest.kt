package com.rescatapp.features.inicio.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.model.Oferta
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
class OfertasNegocioScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val oferta = ofertasDeEjemplo.first().copy(id = 101, nombre = "Pack de prueba")

    @Test
    fun mientrasGuardaMuestraElProgresoYDeshabilitaLasAcciones() {
        mostrar(OfertasNegocioUiState(activas = listOf(oferta), ocupado = true))

        compose.onNodeWithText("Guardando cambios…").assertIsDisplayed()
        compose.onNodeWithText("Pausar").performScrollTo().assertIsNotEnabled()
        compose.onNodeWithText("Editar").assertIsNotEnabled()
    }

    @Test
    fun eliminarSoloEmiteElEventoDespuesDeConfirmar() {
        var eliminaciones = 0
        mostrar(OfertasNegocioUiState(activas = listOf(oferta))) { _, _ -> eliminaciones++ }

        compose.onNodeWithText("Eliminar").performClick()
        compose.onNodeWithText("Cancelar").performClick()
        assertEquals(0, eliminaciones)

        compose.onNodeWithText("Eliminar").performClick()
        compose.onNode(hasText("Eliminar") and hasAnyAncestor(isDialog())).performClick()
        assertEquals(1, eliminaciones)
    }

    @Test
    fun unErrorSeMuestraComoAvisoTemporal() {
        compose.setContent {
            RescatAppTheme {
                OfertasNegocioContenido(
                    estado = OfertasNegocioUiState(error = "Sin conexión"),
                    onSeleccion = {},
                    onEditar = { _, _ -> },
                    onEliminar = { _, _ -> },
                    onCambiarEstado = {},
                    onLimpiarError = {}
                )
            }
        }

        compose.onNodeWithText("Sin conexión").assertIsDisplayed()
    }

    private fun mostrar(
        estado: OfertasNegocioUiState,
        onEliminar: (Oferta, () -> Unit) -> Unit = { _, _ -> }
    ) {
        compose.setContent {
            RescatAppTheme {
                OfertasNegocioContenido(
                    estado = estado,
                    onSeleccion = {},
                    onEditar = { _, _ -> },
                    onEliminar = onEliminar,
                    onCambiarEstado = {},
                    onLimpiarError = {}
                )
            }
        }
    }
}
