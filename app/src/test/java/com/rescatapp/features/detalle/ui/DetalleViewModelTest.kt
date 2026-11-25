package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioCarrito
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class DetalleViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioCarrito = RepositorioCarrito()

    @Test
    fun muestraLaOfertaRecibidaEnLaRuta() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)

        assertEquals("Pack Sorpresa", viewModel.uiState.value.oferta?.nombre)
    }

    @Test
    fun indicaCuandoLaOfertaNoExiste() = runTest {
        val viewModel = crearViewModel(ofertaId = 99)
        observarDuranteLaPrueba(viewModel.uiState)

        assertNull(viewModel.uiState.value.oferta)
    }

    @Test
    fun agregarAlCarritoSumaUnaUnidadYMuestraUnMensaje() = runTest {
        val viewModel = crearViewModel(ofertaId = 13)
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.agregarAlCarrito()
        viewModel.agregarAlCarrito()

        val estado = viewModel.uiState.value
        assertEquals(2, estado.enCarrito)
        assertEquals("Agregaste Pack Croissants al carrito.", estado.mensaje)
    }

    @Test
    fun limpiarMensajeLoOculta() = runTest {
        val viewModel = crearViewModel(ofertaId = 13)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.agregarAlCarrito()

        viewModel.limpiarMensaje()

        assertNull(viewModel.uiState.value.mensaje)
    }

    private fun crearViewModel(ofertaId: Int) = DetalleViewModel(
        savedStateHandle = SavedStateHandle(mapOf(ArgumentosRuta.OFERTA_ID to ofertaId)),
        repositorioOfertas = crearRepositorioOfertas(),
        repositorioCarrito = repositorioCarrito
    )
}
