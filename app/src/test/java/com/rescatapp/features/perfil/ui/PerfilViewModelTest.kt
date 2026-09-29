package com.rescatapp.features.perfil.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PerfilViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioPedidos = RepositorioPedidos()

    @Test
    fun muestraElImpactoDeLosPedidosDeEjemplo() {
        val impacto = crearViewModel().uiState.value.impacto

        assertEquals(4, impacto.rescates)
        assertEquals(102.0, impacto.ahorrado, 0.001)
        assertEquals(3.6, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun cancelarUnPedidoActualizaElImpactoSinRecargar() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)
        val pedidoListo = repositorioPedidos.buscarPorId(5)!!

        repositorioPedidos.actualizar(pedidoListo.copy(estado = EstadoPedido.CANCELADO))

        assertEquals(3, viewModel.uiState.value.impacto.rescates)
    }

    private fun crearViewModel() = PerfilViewModel(repositorioPedidos, CalcularImpactoUseCase())
}
