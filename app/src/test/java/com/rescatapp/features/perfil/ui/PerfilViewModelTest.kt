package com.rescatapp.features.perfil.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearRepositorioPedidos
import com.rescatapp.core.data.local.FakeSesionDao
import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.data.sesionDePrueba
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.RolUsuario
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

    private val api = SupabaseApiFalsa(pedidos = pedidosDeEjemplo)
    private val repositorioPedidos = crearRepositorioPedidos(api = api)

    private val fakeSesionDao = FakeSesionDao()
    private val repositorioAutenticacion = RepositorioAutenticacion(api, fakeSesionDao)

    @Test
    fun muestraElImpactoDeLosPedidosDeEjemplo() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        val impacto = viewModel.uiState.value.impacto
        assertEquals(2, impacto.rescates)
        assertEquals(47.0, impacto.ahorrado, 0.001)
        assertEquals(1.4, impacto.kgAprovechados, 0.001)
    }

    @Test
    fun cancelarUnPedidoNoRecogidoNoModificaElImpacto() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)
        api.pedidos.replaceAll { if (it.id == 5) it.copy(estado = EstadoPedido.CANCELADO) else it }
        repositorioPedidos.sincronizar()

        assertEquals(2, viewModel.uiState.value.impacto.rescates)
    }

    @Test
    fun sesionActivaActualizaNombreCorreoYEstadoAutenticado() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        assertFalse(viewModel.uiState.value.estaAutenticado)

        fakeSesionDao.guardarSesion(
            sesionDePrueba().copy(
                usuarioId = "usr-1",
                nombre = "Daniela Rojas",
                correo = "daniela@example.com",
                rol = RolUsuario.CLIENTE
            )
        )

        assertTrue(viewModel.uiState.value.estaAutenticado)
        assertEquals("Daniela Rojas", viewModel.uiState.value.nombre)
        assertEquals("daniela@example.com", viewModel.uiState.value.correo)
        assertFalse(viewModel.uiState.value.esNegocio)
    }

    @Test
    fun sesionNegocioMuestraNombreNegocio() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        fakeSesionDao.guardarSesion(
            sesionDePrueba().copy(
                usuarioId = "usr-neg",
                nombre = "Carlos Dueño",
                correo = "negocio@comercio.com",
                rol = RolUsuario.NEGOCIO,
                nombreNegocio = "Café del Parque"
            )
        )

        assertTrue(viewModel.uiState.value.estaAutenticado)
        assertTrue(viewModel.uiState.value.esNegocio)
        assertEquals("Café del Parque", viewModel.uiState.value.nombre)
    }

    @Test
    fun cerrarSesionLimpiaLaSesionEnPerfil() = runTest {
        val viewModel = crearViewModel()
        observarDuranteLaPrueba(viewModel.uiState)

        fakeSesionDao.guardarSesion(
            sesionDePrueba().copy(
                usuarioId = "usr-1",
                nombre = "Daniela Rojas",
                correo = "daniela@example.com",
                rol = RolUsuario.CLIENTE
            )
        )
        assertTrue(viewModel.uiState.value.estaAutenticado)

        viewModel.cerrarSesion()

        assertFalse(viewModel.uiState.value.estaAutenticado)
        assertNull(viewModel.uiState.value.sesion)
    }

    private fun crearViewModel() =
        PerfilViewModel(repositorioPedidos, CalcularImpactoUseCase(), repositorioAutenticacion)
}
