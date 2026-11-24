package com.rescatapp.features.registro.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.rescatapp.core.designsystem.theme.RescatAppTheme
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.ErroresRegistro
import com.rescatapp.features.registro.domain.MensajesValidacion
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
class FormularioOfertaTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun permiteIngresarPesoKgYMostrarErrorDeValidacion() {
        var camposActuales by mutableStateOf(CamposRegistro())
        var erroresActuales by mutableStateOf(
            ErroresRegistro(pesoKg = MensajesValidacion.PESO_INVALIDO)
        )

        compose.setContent {
            RescatAppTheme {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    FormularioOferta(
                        campos = camposActuales,
                        errores = erroresActuales,
                        onCampoCambiado = { camposActuales = it }
                    )
                }
            }
        }

        compose.onNodeWithText("Peso estimado (kg)").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText(MensajesValidacion.PESO_INVALIDO)
            .performScrollTo()
            .assertIsDisplayed()

        compose.onNode(hasText("1.5") and hasSetTextAction())
            .performScrollTo()
            .performTextInput("2.5")

        assertEquals("2.5", camposActuales.pesoKg)
    }
}
