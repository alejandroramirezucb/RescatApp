package com.rescatapp.features.perfil.ui

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
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PerfilViewModelTest {
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
    fun emiteElImpactoBase() = runTest {
        val viewModel = PerfilViewModel(RepositorioPedidos(), CalcularImpactoSemanalUseCase())

        val estado = viewModel.uiState.first { it.impacto.reservas == 4 }

        assertEquals(102.0, estado.impacto.ahorro, 0.0)
        assertEquals(3.6, estado.impacto.pesoKg, 0.0)
    }

    @Test
    fun actualizaElImpactoAlCrearUnaReserva() = runTest {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val viewModel = PerfilViewModel(pedidos, CalcularImpactoSemanalUseCase())
        viewModel.uiState.first { it.impacto.reservas == 4 }

        ReservarOfertaUseCase(ofertas, pedidos)(13)

        val estado = viewModel.uiState.first { it.impacto.reservas == 5 }
        assertEquals(117.0, estado.impacto.ahorro, 0.0)
        assertEquals(4.2, estado.impacto.pesoKg, 0.0)
    }
}
