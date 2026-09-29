package com.rescatapp.features.registro.ui

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.MensajesValidacion
import com.rescatapp.features.registro.domain.RegistrarOfertaUseCase
import com.rescatapp.features.registro.domain.ValidarOfertaUseCase
import com.rescatapp.features.registro.domain.camposValidos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegistroViewModelTest {
    private val repositorioOfertas = RepositorioOfertas()
    private val viewModel = RegistroViewModel(
        validarOferta = ValidarOfertaUseCase(),
        registrarOferta = RegistrarOfertaUseCase(repositorioOfertas)
    )

    @Test
    fun publicarDatosValidosGuardaLaOferta() {
        viewModel.onCampoCambiado(camposValidos)

        viewModel.publicar()

        assertTrue(viewModel.uiState.value.guardadoExitoso)
        assertEquals(15, repositorioOfertas.ofertas.value.size)
    }

    @Test
    fun publicarDatosInvalidosMuestraErroresSinGuardar() {
        viewModel.publicar()

        val estado = viewModel.uiState.value
        assertFalse(estado.guardadoExitoso)
        assertEquals(MensajesValidacion.NOMBRE_VACIO, estado.errores.nombre)
        assertEquals(14, repositorioOfertas.ofertas.value.size)
    }

    @Test
    fun corregirUnCampoQuitaSuErrorYMantieneLosDemas() {
        viewModel.publicar()

        viewModel.onCampoCambiado(CamposRegistro(nombre = "Pack Salteñas"))

        val errores = viewModel.uiState.value.errores
        assertNull(errores.nombre)
        assertEquals(MensajesValidacion.COMERCIO_VACIO, errores.comercio)
    }
}
