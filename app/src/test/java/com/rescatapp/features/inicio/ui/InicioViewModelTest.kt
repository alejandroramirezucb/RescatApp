package com.rescatapp.features.inicio.ui

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
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
    fun estadoInicialTieneUsuarioInvitado() = runTest {
        val viewModel = crearViewModel()
        val estado = viewModel.uiState.first()
        assertEquals("Invitado", estado.usuario)
    }

    @Test
    fun iniciaConCatorceOfertasDisponiblesComenzandoPorPackSorpresa() = runTest {
        val viewModel = crearViewModel()

        val estado = viewModel.uiState.first { it.ofertasDisponibles.size == 14 }

        assertEquals(14, estado.ofertasDisponibles.size)
        assertEquals("Pack Sorpresa", estado.ofertasDisponibles.first().nombre)
    }

    @Test
    fun iniciaConCuatroOfertasAgotandoseConLosNombresRequeridos() = runTest {
        val viewModel = crearViewModel()

        val estado = viewModel.uiState.first { it.seEstanAgotando.size == 4 }

        assertEquals(4, estado.seEstanAgotando.size)
        assertEquals(
            listOf("Pizza Familiar", "2 Pizzas Medianas", "Caja Cupcakes", "Burger + Papas"),
            estado.seEstanAgotando.map { it.nombre }
        )
    }

    @Test
    fun calculaElImpactoInicialConLosDatosDeEjemplo() = runTest {
        val viewModel = crearViewModel()

        val estado = viewModel.uiState.first { it.impacto.rescates == 4 }

        assertEquals(4, estado.impacto.rescates)
        assertEquals(102.0, estado.impacto.ahorrado, 0.0)
        assertEquals(3.6, estado.impacto.kgAprovechados, 0.001)
    }

    @Test
    fun incorporaCroissantsCuandoSuDisponibilidadBajaADos() = runTest {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val viewModel = crearViewModel(ofertas, pedidos)
        viewModel.uiState.first { it.seEstanAgotando.size == 4 }

        ReservarOfertaUseCase(ofertas, pedidos)(13)

        val estado = viewModel.uiState.first { it.seEstanAgotando.size == 5 }
        assertTrue(estado.seEstanAgotando.any { it.id == 13 && it.cantidadDisponible == 2 })
        assertEquals(5, estado.impacto.rescates)
    }

    @Test
    fun actualizaElImpactoAlReservarYAlCancelarSinRecargaManual() = runTest {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val viewModel = crearViewModel(ofertas, pedidos)
        val estadoInicial = viewModel.uiState.first { it.impacto.rescates == 4 }
        assertEquals(4, estadoInicial.impacto.rescates)

        val resultado = ReservarOfertaUseCase(ofertas, pedidos)(13)
        val pedidoCreado = (resultado as ResultadoOperacion.Exito<Pedido>).valor

        val estadoReservado = viewModel.uiState.first { it.impacto.rescates == 5 }
        assertEquals(5, estadoReservado.impacto.rescates)
        assertEquals(117.0, estadoReservado.impacto.ahorrado, 0.0)
        assertEquals(4.2, estadoReservado.impacto.kgAprovechados, 0.001)

        pedidos.actualizar(pedidoCreado.copy(estado = EstadoPedido.CANCELADO))

        val estadoCancelado = viewModel.uiState.first { it.impacto.rescates == 4 }
        assertEquals(4, estadoCancelado.impacto.rescates)
        assertEquals(102.0, estadoCancelado.impacto.ahorrado, 0.0)
        assertEquals(3.6, estadoCancelado.impacto.kgAprovechados, 0.001)
    }

    @Test
    fun conListaDePedidosVaciaElImpactoEsCero() = runTest {
        val viewModel = crearViewModel(pedidos = RepositorioPedidos(emptyList()))

        val estado = viewModel.uiState.first()

        assertEquals(0, estado.impacto.rescates)
        assertEquals(0.0, estado.impacto.ahorrado, 0.0)
        assertEquals(0.0, estado.impacto.kgAprovechados, 0.0)
    }

    @Test
    fun excluyeUnaOfertaCuandoSeAgota() = runTest {
        val ofertas = RepositorioOfertas()
        val pedidos = RepositorioPedidos()
        val croissants = ofertas.obtenerActualPorId(13)!!
        ofertas.actualizar(croissants.copy(cantidadDisponible = 1))
        val viewModel = crearViewModel(ofertas, pedidos)
        viewModel.uiState.first { it.seEstanAgotando.any { oferta -> oferta.id == 13 } }

        ReservarOfertaUseCase(ofertas, pedidos)(13)

        val estado = viewModel.uiState.first { estado ->
            estado.ofertasDisponibles.none { oferta -> oferta.id == 13 } &&
                estado.seEstanAgotando.none { oferta -> oferta.id == 13 }
        }
        assertFalse(estado.ofertasDisponibles.any { it.id == 13 })
        assertFalse(estado.seEstanAgotando.any { it.id == 13 })
    }

    private fun crearViewModel(
        ofertas: RepositorioOfertas = RepositorioOfertas(),
        pedidos: RepositorioPedidos = RepositorioPedidos()
    ) = InicioViewModel(ofertas, pedidos, CalcularImpactoUseCase())
}
