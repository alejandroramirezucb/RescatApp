package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.explorar.domain.TipoBusquedaExplorar
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ExplorarNegociosViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    @Test
    fun buscaNegociosYPermiteVerSusOfertasActivas() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.seleccionarTipoBusqueda(TipoBusquedaExplorar.NEGOCIOS)
        viewModel.buscar("café")

        assertEquals(0, viewModel.uiState.value.cantidad)
        assertEquals(
            listOf("Café Aroma"),
            viewModel.uiState.value.comerciosVisibles.map { it.nombre }
        )

        viewModel.buscar("")
        viewModel.seleccionarComercio("Café Aroma")

        assertEquals(3, viewModel.uiState.value.cantidad)
        assertTrue(viewModel.uiState.value.comerciosVisibles.isEmpty())
        assertTrue(viewModel.uiState.value.ofertasVisibles.all { it.comercio == "Café Aroma" })
    }

    @Test
    fun sinCoincidenciasMarcaSinResultados() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.buscar("ComercioInexistenteXYZ")

        assertTrue(viewModel.uiState.value.sinResultados)
        assertEquals(0, viewModel.uiState.value.cantidad)
        assertEquals(0, viewModel.uiState.value.cantidadComercios)
    }

    @Test
    fun aplicaComercioYTipoRecibidosEnLaRuta() = runTest {
        val viewModel = crearViewModel(
            comercio = "Café Aroma",
            tipo = TipoBusquedaExplorar.NEGOCIOS.name
        )
        observarDuranteLaPrueba(viewModel.uiState)

        assertEquals("Café Aroma", viewModel.uiState.value.filtros.comercioSeleccionado)
        assertEquals(3, viewModel.uiState.value.cantidad)
    }

    private fun crearViewModel(comercio: String? = null, tipo: String? = null) = ExplorarViewModel(
        savedStateHandle = SavedStateHandle(
            mapOf(ArgumentosRuta.COMERCIO to comercio, ArgumentosRuta.TIPO to tipo)
        ),
        repositorioOfertas = crearRepositorioOfertas()
    )
}
