package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.inicio.domain.EditarOfertaUseCase
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ExplorarViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioOfertas = crearRepositorioOfertas()

    @Test
    fun iniciaConTodasLasOfertas() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        assertEquals(14, viewModel.uiState.value.cantidad)
    }

    @Test
    fun aplicaLaCategoriaRecibidaEnLaRuta() = runTest {
        val viewModel = crearViewModel(categoria = Categoria.POSTRES.name)
        observarDuranteLaPrueba(viewModel.uiState)

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

    @Test
    fun buscaPorComercioYRecuperaTodasAlQuitarLosFiltros() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.buscar("café")
        assertEquals(3, viewModel.uiState.value.cantidad)

        viewModel.seleccionarCategoria(Categoria.POSTRES)
        assertEquals(0, viewModel.uiState.value.cantidad)

        viewModel.seleccionarCategoria(null)
        viewModel.buscar("")
        assertEquals(14, viewModel.uiState.value.cantidad)
    }

    @Test
    fun mantieneElOrdenAlCambiarLaCategoriaYActualizaElResultado() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.seleccionarOrden(OrdenOfertas.MAYOR_DESCUENTO)
        viewModel.seleccionarCategoria(Categoria.POSTRES)

        val nueva = repositorioOfertas.agregar(
            ofertasDeEjemplo.first().copy(
                nombre = "Postre nuevo",
                categoria = Categoria.POSTRES,
                precioNormal = 100.0,
                precioRescate = 10.0
            )
        )

        assertEquals(OrdenOfertas.MAYOR_DESCUENTO, viewModel.uiState.value.filtros.orden)
        assertEquals(nueva.id, viewModel.uiState.value.ofertas.first().id)
        assertEquals(3, viewModel.uiState.value.cantidad)
    }

    @Test
    fun editarActualizaLaConsultaActivaSinRecargarYConservaLosFiltros() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.buscar("Edicion Room")
        viewModel.seleccionarCategoria(Categoria.POSTRES)
        viewModel.seleccionarOrden(OrdenOfertas.MAYOR_DESCUENTO)
        val editarOferta = EditarOfertaUseCase(repositorioOfertas)
        val oferta = ofertasDeEjemplo.first()

        editarOferta(oferta.copy(nombre = "Edicion Room", categoria = Categoria.POSTRES))

        assertEquals(listOf(oferta.id), viewModel.uiState.value.ofertas.map { it.id })
        assertEquals("Edicion Room", viewModel.uiState.value.filtros.texto)
        assertEquals(Categoria.POSTRES, viewModel.uiState.value.filtros.categoria)
        assertEquals(OrdenOfertas.MAYOR_DESCUENTO, viewModel.uiState.value.filtros.orden)

        editarOferta(oferta.copy(nombre = "Edicion Room", categoria = Categoria.PANADERIA))

        assertEquals(0, viewModel.uiState.value.cantidad)
    }

    private fun crearViewModel(categoria: String? = null) = ExplorarViewModel(
        savedStateHandle = SavedStateHandle(mapOf(ArgumentosRuta.CATEGORIA to categoria)),
        repositorioOfertas = repositorioOfertas
    )
}
