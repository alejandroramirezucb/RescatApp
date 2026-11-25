package com.rescatapp.features.inicio.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearAutenticacion
import com.rescatapp.core.data.crearRepositorioNotificaciones
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.crearRepositorioPedidos
import com.rescatapp.core.data.pedidosDeEjemplo
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

    private val api = SupabaseApiFalsa(pedidos = pedidosDeEjemplo)
    private val repositorioOfertas = crearRepositorioOfertas(api = api)
    private val repositorioPedidos = crearRepositorioPedidos(api = api)

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

        repositorioPedidos.reservar(13)
        repositorioOfertas.sincronizar()

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
        repositorioNotificaciones = crearRepositorioNotificaciones(api = api),
        repositorioAutenticacion = crearAutenticacion(api, sesion = null)
    )
}
