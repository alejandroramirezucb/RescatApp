package com.rescatapp.features.registro.ui

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.MensajeError
import com.rescatapp.features.registro.domain.RegistrarOfertaUseCase
import com.rescatapp.features.registro.domain.ValidarOfertaUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RegistroViewModelTest {

    private lateinit var repositorio: RepositorioOfertas
    private lateinit var viewModel: RegistroViewModel

    @Before
    fun setUp() {
        repositorio = RepositorioOfertas()
        val validarUseCase = ValidarOfertaUseCase()
        val registrarUseCase = RegistrarOfertaUseCase(repositorio, validarUseCase)
        viewModel = RegistroViewModel(validarUseCase, registrarUseCase)
    }

    @Test
    fun datosValidos_publicarGuardaOfertaYNotificaGuardadoExitoso() {
        val totalInicial = repositorio.ofertas.value.size
        val camposValidos = CamposRegistro(
            nombre = "Pack Salteñas",
            comercio = "Panadería La Central",
            categoria = Categoria.PANADERIA,
            descripcion = "Salteñas del día",
            pesoKg = "1,5",
            precioNormal = "40",
            precioRescate = "20",
            cantidadDisponible = "4",
            horaRetiroDesde = "09:00",
            horaRetiroHasta = "11:00"
        )

        viewModel.onCampoCambiado(camposValidos)
        viewModel.publicar()

        val state = viewModel.uiState.value
        assertTrue(state.guardadoExitoso)
        assertFalse(state.errores.hayErrores)
        assertEquals(totalInicial + 1, repositorio.ofertas.value.size)

        val ofertaGuardada = repositorio.ofertas.value.first()
        assertEquals("Pack Salteñas", ofertaGuardada.nombre)
        assertEquals("Panadería La Central", ofertaGuardada.comercio)
        assertEquals(Categoria.PANADERIA, ofertaGuardada.categoria)
        assertEquals(1.5, ofertaGuardada.pesoKg, 0.0)
        assertEquals(4, ofertaGuardada.cantidadDisponible)
        assertTrue(ofertaGuardada.id > 14)
    }

    @Test
    fun datosInvalidos_publicarNoInvocaRepositorioYPermaneceEnFormulario() {
        val totalInicial = repositorio.ofertas.value.size
        val camposInvalidos = CamposRegistro(nombre = "")

        viewModel.onCampoCambiado(camposInvalidos)
        viewModel.publicar()

        val state = viewModel.uiState.value
        assertFalse(state.guardadoExitoso)
        assertTrue(state.errores.hayErrores)
        assertEquals(totalInicial, repositorio.ofertas.value.size)
    }

    @Test
    fun unCampoConError_alIngresarNuevoValor_eliminaErrorDeEseCampoYMantieneLosDemas() {
        // Al intentar publicar campos vacíos se generan errores
        viewModel.publicar()

        val stateConErrores = viewModel.uiState.value
        assertNotNull(stateConErrores.errores.nombre)
        assertNotNull(stateConErrores.errores.comercio)
        assertEquals(MensajeError.NOMBRE_VACIO, stateConErrores.errores.nombre)
        assertEquals(MensajeError.COMERCIO_VACIO, stateConErrores.errores.comercio)

        // El usuario ingresa un nuevo valor únicamente en el nombre
        viewModel.onCampoCambiado(nombre = "Pack Salteñas")

        val stateActualizado = viewModel.uiState.value
        // El error de nombre se elimina inmediatamente
        assertNull(stateActualizado.errores.nombre)
        // El error de comercio se mantiene
        assertEquals(MensajeError.COMERCIO_VACIO, stateActualizado.errores.comercio)
    }

    @Test
    fun metodosIndividuales_actualizanCampoYEliminanSoloSuError() {
        viewModel.publicar()
        assertNotNull(viewModel.uiState.value.errores.comercio)
        assertNotNull(viewModel.uiState.value.errores.descripcion)

        viewModel.onComercioCambiado("Panadería La Central")

        val state = viewModel.uiState.value
        assertEquals("Panadería La Central", state.campos.comercio)
        assertNull(state.errores.comercio)
        assertNotNull(state.errores.descripcion)
    }
}
