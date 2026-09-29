package com.rescatapp.features.inicio.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.features.inicio.domain.SeleccionarOfertasInicioUseCase
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class InicioViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioOfertas = RepositorioOfertas()
    private val repositorioPedidos = RepositorioPedidos()

    @Test
    fun estadoInicialTieneUsuarioInvitado() {
        assertEquals("Invitado", crearViewModel().uiState.value.usuario)
    }

    @Test
    fun iniciaConElImpactoYLasSeccionesDelDiseno() {
        val estado = crearViewModel().uiState.value

        assertEquals(4, estado.impacto.rescates)
        assertEquals(14, estado.ofertas.disponibles.size)
        assertEquals(4, estado.ofertas.porAgotarse.size)
    }

    @Test
    fun unaReservaActualizaImpactoYSeccionesSinRecargar() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)
        val packCroissants = repositorioOfertas.buscarPorId(13)!!

        repositorioOfertas.cambiarUnidadesDisponibles(packCroissants.id, diferencia = -1)
        repositorioPedidos.agregar(packCroissants.crearPedido(idPedido = 0))

        val estado = viewModel.uiState.value
        assertEquals(5, estado.impacto.rescates)
        assertEquals(117.0, estado.impacto.ahorrado, 0.001)
        assertEquals(4.2, estado.impacto.kgAprovechados, 0.001)
        assertEquals(5, estado.ofertas.porAgotarse.size)
    }

    private fun crearViewModel() = InicioViewModel(
        repositorioOfertas = repositorioOfertas,
        repositorioPedidos = repositorioPedidos,
        calcularImpacto = CalcularImpactoUseCase(),
        seleccionarOfertas = SeleccionarOfertasInicioUseCase()
    )
}
