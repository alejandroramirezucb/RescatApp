package com.rescatapp.features.inicio.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.core.domain.ValidadorCamposOferta
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
class OfertasNegocioEdicionScreenTest {
    @get:Rule
    val compose = createComposeRule()

    private val oferta = ofertasDeEjemplo.first().copy(id = 101, nombre = "Pack de prueba")

    @Test
    @GraphicsMode(GraphicsMode.Mode.LEGACY)
    fun editarUnaOfertaRemotaValidaElPrecioAntesDeEmitirElEvento() {
        var ediciones = 0
        val ofertaActualizada = mutableStateOf<Oferta?>(null)
        compose.setContent {
            RescatAppTheme {
                if (ofertaActualizada.value == null) {
                    EditorOferta(
                        oferta,
                        onGuardar = {
                            ofertaActualizada.value = it
                            ediciones++
                        }
                    ) { onConfirmar, contenido ->
                        Column {
                            contenido()
                            Button(onClick = onConfirmar) { Text("Guardar cambios") }
                        }
                    }
                } else {
                    Text("Oferta actualizada")
                }
            }
        }
        compose.onNodeWithText(oferta.precioRescate.toString()).performTextReplacement("99999")
        compose.onNodeWithText("Guardar cambios").performClick()
        assertEquals(0, ediciones)
        compose.onNodeWithText(ValidadorCamposOferta.PRECIO_RESCATE_NO_MENOR).assertIsDisplayed()

        compose.onNodeWithText("99999").performTextReplacement("1")
        compose.onNodeWithText("Guardar cambios").performClick()
        assertEquals(1, ediciones)
        assertEquals(1.0, ofertaActualizada.value!!.precioRescate, 0.001)
        compose.onNodeWithText("Oferta actualizada").assertIsDisplayed()
    }

    @Test
    fun eliminarLocalMuestraElNombreYVolverNoEliminaLaOferta() {
        var eliminacionesLocales = 0
        var eliminacionesRemotas = 0
        val estado = mutableStateOf(OfertasNegocioUiState(activas = listOf(oferta)))
        compose.setContent {
            RescatAppTheme {
                ContenidoPrueba(
                    estado.value,
                    onEliminar = { eliminacionesRemotas++ },
                    onEliminarLocal = { _, onEliminada ->
                        eliminacionesLocales++
                        estado.value = estado.value.copy(activas = emptyList())
                        onEliminada()
                    }
                )
            }
        }
        compose.onNodeWithText("Eliminar").performClick()
        compose.onNodeWithText("¿Eliminar «${oferta.nombre}» de las ofertas locales?")
            .assertIsDisplayed()
        compose.onNodeWithText("Volver").performClick()
        assertEquals(0, eliminacionesLocales)
        compose.onNodeWithText(oferta.nombre).assertIsDisplayed()

        compose.onNodeWithText("Eliminar").performClick()
        compose.onNode(hasText("Eliminar") and hasAnyAncestor(isDialog())).performClick()

        assertEquals(1, eliminacionesLocales)
        assertEquals(0, eliminacionesRemotas)
        compose.onNodeWithText(oferta.nombre).assertDoesNotExist()
        compose.onNodeWithText("No hay ofertas activas").assertIsDisplayed()
    }
}
