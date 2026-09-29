package com.rescatapp.features.inicio.ui

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoSemanalUseCase
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
class InicioViewModelTest {
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
    fun iniciaConCuatroOfertasAgotandose() = runTest {
        val viewModel = crearViewModel()

        val estado = viewModel.uiState.first { it.ofertasAgotando.size == 4 }

        assertEquals(4, estado.ofertasAgotando.size)
    }

    @Test
    fun incorporaCroissantsCuandoSuDisponibilidadBajaADos() = runTest {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val viewModel = crearViewModel(ofertas, pedidos)
        viewModel.uiState.first { it.ofertasAgotando.size == 4 }

        ReservarOfertaUseCase(ofertas, pedidos)(13)

        val estado = viewModel.uiState.first { it.ofertasAgotando.size == 5 }
        assertTrue(estado.ofertasAgotando.any { it.id == 13 && it.cantidadDisponible == 2 })
        assertEquals(5, estado.impacto.reservas)
    }

    @Test
    fun excluyeUnaOfertaCuandoSeAgota() = runTest {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val croissants = ofertas.obtenerActualPorId(13)!!
        ofertas.actualizar(croissants.copy(cantidadDisponible = 1))
        val viewModel = crearViewModel(ofertas, pedidos)
        viewModel.uiState.first { it.ofertasAgotando.any { oferta -> oferta.id == 13 } }

        ReservarOfertaUseCase(ofertas, pedidos)(13)

        val estado = viewModel.uiState.first { estado ->
            estado.ofertasCerca.none { oferta -> oferta.id == 13 } &&
                estado.ofertasAgotando.none { oferta -> oferta.id == 13 }
        }
        assertFalse(estado.ofertasCerca.any { it.id == 13 })
        assertFalse(estado.ofertasAgotando.any { it.id == 13 })
    }

    private fun crearViewModel(
        ofertas: RepositorioOfertas = RepositorioOfertas(),
        pedidos: RepositorioPedidos = RepositorioPedidos()
    ) = InicioViewModel(ofertas, pedidos, CalcularImpactoSemanalUseCase())
}
