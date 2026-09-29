package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.explorar.domain.FiltrarOfertasUseCase
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ExplorarViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioOfertas = RepositorioOfertas()

    @Test
    fun iniciaConTodasLasOfertas() {
        assertEquals(14, crearViewModel().uiState.value.cantidad)
    }

    @Test
    fun aplicaLaCategoriaRecibidaEnLaRuta() {
        val viewModel = crearViewModel(categoria = Categoria.POSTRES.name)

        assertEquals(Categoria.POSTRES, viewModel.uiState.value.filtros.categoria)
        assertEquals(2, viewModel.uiState.value.cantidad)
    }

    @Test
    fun buscarYOrdenarActualizanElListado() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.buscar("pack")
        viewModel.seleccionarOrden(OrdenOfertas.MAYOR_DESCUENTO)

        assertEquals("Pack Frutas", viewModel.uiState.value.ofertas.first().nombre)
    }

    @Test
    fun unaOfertaPublicadaApareceSinRecargar() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        repositorioOfertas.agregar(ofertasDeEjemplo.first().copy(nombre = "Pack Salteñas"))

        assertEquals(15, viewModel.uiState.value.cantidad)
        assertEquals("Pack Salteñas", viewModel.uiState.value.ofertas.first().nombre)
    }

    private fun crearViewModel(categoria: String? = null) = ExplorarViewModel(
        savedStateHandle = SavedStateHandle(mapOf(ArgumentosRuta.CATEGORIA to categoria)),
        repositorioOfertas = repositorioOfertas,
        filtrarOfertas = FiltrarOfertasUseCase()
    )
}
