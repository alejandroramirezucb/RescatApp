package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetalleViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioOfertas = crearRepositorioOfertas()

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
        assertTrue(viewModel.uiState.value.noEncontrada)
    }

    @Test
    fun reservarActualizaLaCantidadYMuestraUnMensaje() = runTest {
        val viewModel = crearViewModel(ofertaId = 13)
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.reservar()

        val estado = viewModel.uiState.value
        assertEquals(2, estado.oferta?.cantidadDisponible)
        assertEquals("Reservaste Pack Croissants. Puedes verlo en Pedidos.", estado.mensaje)
    }

    @Test
    fun limpiarMensajeLoOculta() = runTest {
        val viewModel = crearViewModel(ofertaId = 13)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.reservar()

        viewModel.limpiarMensaje()

        assertNull(viewModel.uiState.value.mensaje)
    }

    private fun crearViewModel(ofertaId: Int) = DetalleViewModel(
        savedStateHandle = SavedStateHandle(mapOf(ArgumentosRuta.OFERTA_ID to ofertaId)),
        repositorioOfertas = repositorioOfertas,
        reservarOferta = ReservarOfertaUseCase(repositorioOfertas, RepositorioPedidos(FakePedidoDao()))
    )
}
