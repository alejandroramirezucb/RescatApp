package com.rescatapp.features.inicio.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.FakeNotificacionDao
import com.rescatapp.core.data.local.FakePedidoDao
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

    private val repositorioOfertas = crearRepositorioOfertas()
    private val repositorioPedidos = RepositorioPedidos(FakePedidoDao())

    @Test
    fun estadoInicialTieneUsuarioInvitado() = runTest {
        assertEquals("Invitado", crearViewModel().uiState.value.usuario)
    }

    @Test
    fun iniciaConElImpactoYLasSeccionesDelDiseno() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)
        val estado = viewModel.uiState.value

        assertEquals(2, estado.impacto.rescates)
        assertEquals(14, estado.ofertas.disponibles.size)
        assertEquals(4, estado.ofertas.porAgotarse.size)
    }

    @Test
    fun unaReservaActualizaSeccionesPeroNoGeneraImpactoHastaRecogerse() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)
        val packCroissants = repositorioOfertas.buscarPorId(13)!!

        repositorioOfertas.cambiarUnidadesDisponibles(packCroissants.id, diferencia = -1)
        repositorioPedidos.agregar(packCroissants.crearPedido(idPedido = 0))

        val estado = viewModel.uiState.value
        assertEquals(2, estado.impacto.rescates)
        assertEquals(47.0, estado.impacto.ahorrado, 0.001)
        assertEquals(1.4, estado.impacto.kgAprovechados, 0.001)
        assertEquals(5, estado.ofertas.porAgotarse.size)
    }

    private fun crearViewModel() = InicioViewModel(
        repositorioOfertas = repositorioOfertas,
        repositorioPedidos = repositorioPedidos,
        calcularImpacto = CalcularImpactoUseCase(),
        seleccionarOfertas = SeleccionarOfertasInicioUseCase(),
        repositorioNotificaciones = RepositorioNotificaciones(FakeNotificacionDao())
    )
}
