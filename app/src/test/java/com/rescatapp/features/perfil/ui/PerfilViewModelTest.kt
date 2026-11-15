package com.rescatapp.features.perfil.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.local.FakeUsuarioDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
import com.rescatapp.features.auth.domain.model.SesionUsuario
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class PerfilViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val fakePedidoDao = FakePedidoDao()
    private val repositorioPedidos = RepositorioPedidos(fakePedidoDao)

    private val fakeUsuarioDao = FakeUsuarioDao(UsuariosDeEjemplo.todos.map { it.toEntity() })
    private val fakeSesionDao = FakeSesionDao()
    private val repositorioAutenticacion = RepositorioAutenticacion(fakeUsuarioDao, fakeSesionDao)

    @Test
    fun muestraElImpactoDeLosPedidosDeEjemplo() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        val impacto = viewModel.uiState.value.impacto
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

    @Test
    fun sesionActivaActualizaNombreCorreoYEstadoAutenticado() = runTest {
        val viewModel =
            PerfilViewModel(repositorioPedidos, CalcularImpactoUseCase(), repositorioAutenticacion)
        observarDuranteLaPrueba(viewModel.uiState)

        assertFalse(viewModel.uiState.value.estaAutenticado)

        fakeSesionDao.guardarSesion(
            SesionUsuario(
                usuarioId = "usr-1",
                nombre = "Daniela Rojas",
                correo = "daniela@example.com",
                rol = RolUsuario.CLIENTE
            ).toEntity()
        )

        assertTrue(viewModel.uiState.value.estaAutenticado)
        assertEquals("Daniela Rojas", viewModel.uiState.value.nombre)
        assertEquals("daniela@example.com", viewModel.uiState.value.correo)
        assertFalse(viewModel.uiState.value.esNegocio)
    }

    @Test
    fun sesionNegocioMuestraNombreNegocio() = runTest {
        val viewModel =
            PerfilViewModel(repositorioPedidos, CalcularImpactoUseCase(), repositorioAutenticacion)
        observarDuranteLaPrueba(viewModel.uiState)

        fakeSesionDao.guardarSesion(
            SesionUsuario(
                usuarioId = "usr-neg",
                nombre = "Carlos Dueño",
                correo = "negocio@comercio.com",
                rol = RolUsuario.NEGOCIO,
                nombreNegocio = "Café del Parque"
            ).toEntity()
        )

        assertTrue(viewModel.uiState.value.estaAutenticado)
        assertTrue(viewModel.uiState.value.esNegocio)
        assertEquals("Café del Parque", viewModel.uiState.value.nombre)
    }

    @Test
    fun cerrarSesionLimpiaLaSesionEnPerfil() = runTest {
        val viewModel =
            PerfilViewModel(repositorioPedidos, CalcularImpactoUseCase(), repositorioAutenticacion)
        observarDuranteLaPrueba(viewModel.uiState)

        fakeSesionDao.guardarSesion(
            SesionUsuario(
                usuarioId = "usr-1",
                nombre = "Daniela Rojas",
                correo = "daniela@example.com",
                rol = RolUsuario.CLIENTE
            ).toEntity()
        )
        assertTrue(viewModel.uiState.value.estaAutenticado)

        viewModel.cerrarSesion()

        assertFalse(viewModel.uiState.value.estaAutenticado)
        assertNull(viewModel.uiState.value.sesion)
    }

    private fun crearViewModel() = PerfilViewModel(repositorioPedidos, CalcularImpactoUseCase())
}
