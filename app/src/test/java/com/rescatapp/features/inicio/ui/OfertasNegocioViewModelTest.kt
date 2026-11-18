package com.rescatapp.features.inicio.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.USUARIO_PRUEBA
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.errorServidor
import com.rescatapp.core.data.ofertasDeEjemplo
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OfertasNegocioViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val propias = ofertasDeEjemplo.take(3).map { it.copy(propietarioId = USUARIO_PRUEBA) }
    private val api = SupabaseApiFalsa(propias + ofertasDeEjemplo.drop(3))
    private val repositorio = crearRepositorioOfertas(api = api)
    private val viewModel by lazy {
        OfertasNegocioViewModel(repositorio)
    }

    @Test
    fun separaLasOfertasPropiasActivasDeLasFinalizadas() = runTest {
        api.ofertas.replaceAll { if (it.id == 14) it.copy(estadoPublicacion = "PAUSADA") else it }
        repositorio.sincronizar()

        assertEquals(listOf(13, 12), viewModel.uiState.value.activas.map { it.id })
        assertEquals(listOf(14), viewModel.uiState.value.finalizadas.map { it.id })
    }

    @Test
    fun cambiarEstadoPausaLaOfertaYLaPasaAFinalizadas() = runTest {
        viewModel.cambiarEstado(propias.first())

        assertEquals(listOf(14), viewModel.uiState.value.finalizadas.map { it.id })
        assertFalse(viewModel.uiState.value.ocupado)
    }

    @Test
    fun eliminarQuitaLaOfertaYAvisaAlTerminar() = runTest {
        var eliminada = false

        viewModel.eliminarOferta(propias.first()) { eliminada = true }

        assertTrue(eliminada)
        assertTrue(viewModel.uiState.value.activas.none { it.id == 14 })
    }

    @Test
    fun unErrorDelServidorSeMuestraYSePuedeLimpiar() = runTest {
        api.fallo = errorServidor("La oferta ya no existe o no te pertenece.")

        viewModel.actualizarOferta(propias.first())

        assertEquals("La oferta ya no existe o no te pertenece.", viewModel.uiState.value.error)
        viewModel.limpiarError()
        assertEquals(null, viewModel.uiState.value.error)
    }
}
