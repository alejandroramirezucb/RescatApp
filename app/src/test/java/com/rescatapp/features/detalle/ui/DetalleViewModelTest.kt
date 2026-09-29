package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()

    @Before
    fun prepararDispatcherPrincipal() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun restaurarDispatcherPrincipal() {
        Dispatchers.resetMain()
    }

    @Test
    fun cargaLaOfertaIndicadaPorLaRuta() = runTest {
        val viewModel = crearViewModel(14)

        val estado = viewModel.uiState.first { it.oferta?.id == 14 }

        assertEquals("Pack Sorpresa", estado.oferta?.nombre)
        assertFalse(estado.noEncontrada)
    }

    @Test
    fun muestraEstadoNoEncontradoParaUnIdInexistente() = runTest {
        val viewModel = crearViewModel(999)

        val estado = viewModel.uiState.first { it.noEncontrada }

        assertTrue(estado.noEncontrada)
        assertEquals(null, estado.oferta)
    }

    @Test
    fun reservaYActualizaLaDisponibilidadEnElMismoEstado() = runTest {
        val viewModel = crearViewModel(14)
        viewModel.uiState.first { it.oferta?.id == 14 }

        viewModel.reservar()

        val estado = viewModel.uiState.first {
            it.oferta?.cantidadDisponible == 4 &&
                it.mensaje == "Oferta reservada. Puedes verla en Pedidos."
        }
        assertEquals(4, estado.oferta?.cantidadDisponible)
        assertEquals("Oferta reservada. Puedes verla en Pedidos.", estado.mensaje)
    }

    @Test
    fun informaCuandoLaOfertaEstaAgotada() = runTest {
        val repositorioOfertas = RepositorioOfertas()
        val oferta = repositorioOfertas.obtenerActualPorId(14)!!
        repositorioOfertas.actualizar(oferta.copy(cantidadDisponible = 0))
        val viewModel = crearViewModel(14, repositorioOfertas)
        viewModel.uiState.first { it.oferta?.estaAgotada == true }

        viewModel.reservar()

        val estado = viewModel.uiState.first { it.mensaje != null }
        assertEquals("Esta oferta está agotada", estado.mensaje)
    }

    @Test
    fun reservaLaUltimaUnidadYElEstadoQuedaAgotado() = runTest {
        val repositorioOfertas = RepositorioOfertas()
        val oferta = repositorioOfertas.obtenerActualPorId(14)!!
        repositorioOfertas.actualizar(oferta.copy(cantidadDisponible = 1))
        val viewModel = crearViewModel(14, repositorioOfertas)
        viewModel.uiState.first { it.oferta?.cantidadDisponible == 1 }

        viewModel.reservar()

        val estado = viewModel.uiState.first { it.oferta?.estaAgotada == true }
        assertTrue(estado.oferta?.estaAgotada == true)
        assertEquals("Oferta reservada. Puedes verla en Pedidos.", estado.mensaje)
    }

    private fun crearViewModel(
        ofertaId: Int,
        repositorioOfertas: RepositorioOfertas = RepositorioOfertas()
    ): DetalleViewModel = DetalleViewModel(
        SavedStateHandle(mapOf("ofertaId" to ofertaId)),
        repositorioOfertas,
        ReservarOfertaUseCase(repositorioOfertas, RepositorioPedidos())
    )
}
