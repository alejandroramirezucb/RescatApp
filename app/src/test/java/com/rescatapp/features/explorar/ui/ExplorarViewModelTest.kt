package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.core.data.RepositorioOfertas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExplorarViewModelTest {
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
    fun conservaUnaOfertaAgotadaYEmiteSuNuevaDisponibilidad() = runTest {
        val repositorio = RepositorioOfertas()
        val viewModel = ExplorarViewModel(repositorio, SavedStateHandle())
        viewModel.uiState.first { it.ofertas.any { oferta -> oferta.id == 13 } }
        val croissants = repositorio.obtenerActualPorId(13)!!

        repositorio.actualizar(croissants.copy(cantidadDisponible = 0))

        val estado = viewModel.uiState.first { uiState ->
            uiState.ofertas.find { oferta -> oferta.id == 13 }?.estaAgotada == true
        }
        val croissantsActualizados = estado.ofertas.find { it.id == 13 }
        assertEquals(0, croissantsActualizados?.cantidadDisponible)
        assertTrue(croissantsActualizados?.estaAgotada == true)
    }
}
