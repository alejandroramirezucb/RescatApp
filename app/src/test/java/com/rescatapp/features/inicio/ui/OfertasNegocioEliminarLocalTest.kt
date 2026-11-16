package com.rescatapp.features.inicio.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.OfertaDaoFalso
import com.rescatapp.core.data.RepositorioNotificaciones
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.FakeNotificacionDao
import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.OfertaEntity
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.domain.CalcularImpactoUseCase
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.explorar.ui.ExplorarViewModel
import com.rescatapp.features.inicio.domain.EditarOfertaUseCase
import com.rescatapp.features.inicio.domain.SeleccionarOfertasInicioUseCase
import com.rescatapp.observarDuranteLaPrueba
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OfertasNegocioEliminarLocalTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val oferta = ofertasDeEjemplo.first().copy(comercio = UsuarioDemo.NEGOCIO_DEMO)
    private val local = crearRepositorioOfertas(listOf(oferta))
    private val remoto = FuenteRemotaFalsa(mutableListOf(oferta.copy(id = 101)))

    private fun crearViewModel() =
        OfertasNegocioViewModel(local, remoto, EditarOfertaUseCase(local))

    @Test
    fun eliminarLocalActualizaMisOfertasInicioYExplorarSinTocarLasRemotas() = runTest {
        val vm = crearViewModel()
        val inicio = InicioViewModel(
            local,
            RepositorioPedidos(FakePedidoDao()),
            CalcularImpactoUseCase(),
            SeleccionarOfertasInicioUseCase(),
            RepositorioNotificaciones(FakeNotificacionDao())
        )
        val explorar = ExplorarViewModel(SavedStateHandle(), local)
        observarDuranteLaPrueba(inicio.uiState)
        observarDuranteLaPrueba(explorar.uiState)
        vm.cargarOfertasRemotas()
        assertEquals(listOf(oferta), vm.uiState.value.activas)
        assertEquals(listOf(oferta), inicio.uiState.value.ofertas.disponibles)
        assertEquals(listOf(oferta), explorar.uiState.value.ofertas)

        vm.eliminarOfertaLocal(oferta)

        assertTrue(vm.uiState.value.activas.isEmpty())
        assertTrue(vm.uiState.value.finalizadas.isEmpty())
        assertTrue(inicio.uiState.value.ofertas.disponibles.isEmpty())
        assertTrue(inicio.uiState.value.ofertas.porAgotarse.isEmpty())
        assertTrue(explorar.uiState.value.ofertas.isEmpty())
        assertNull(local.buscarPorId(oferta.id))
        assertEquals(1, ofertasEnLinea(vm).size)
        assertEquals(0, remoto.mutaciones)
    }

    @Test
    fun eliminarLocalEsperaRoomYBloqueaConfirmacionesDuplicadas() = runTest {
        val respuesta = CompletableDeferred<Unit>()
        val base = OfertaDaoFalso(listOf(oferta))
        var escrituras = 0
        val dao = object : OfertaDao by base {
            override suspend fun eliminar(oferta: OfertaEntity) {
                escrituras++
                respuesta.await()
                base.eliminar(oferta)
            }
        }
        val repo = RepositorioOfertas(dao)
        val vm = OfertasNegocioViewModel(repo, remoto, EditarOfertaUseCase(repo))
        var confirmaciones = 0
        vm.eliminarOfertaLocal(oferta) { confirmaciones++ }
        vm.eliminarOfertaLocal(oferta) { confirmaciones++ }
        assertTrue(vm.uiState.value.eliminandoLocal)
        assertTrue(vm.uiState.value.ocupado)
        assertEquals(0, confirmaciones)
        assertEquals(listOf(oferta), vm.uiState.value.activas)

        respuesta.complete(Unit)

        assertEquals(1, escrituras)
        assertEquals(1, confirmaciones)
        assertFalse(vm.uiState.value.ocupado)
        assertTrue(vm.uiState.value.activas.isEmpty())
    }

    @Test
    fun errorAlEliminarConservaLaOfertaYPermiteReintentar() = runTest {
        val base = OfertaDaoFalso(listOf(oferta))
        var fallar = true
        val dao = object : OfertaDao by base {
            override suspend fun eliminar(oferta: OfertaEntity) {
                if (fallar) throw IOException("Error de escritura")
                base.eliminar(oferta)
            }
        }
        val repo = RepositorioOfertas(dao)
        val vm = OfertasNegocioViewModel(repo, remoto, EditarOfertaUseCase(repo))
        var confirmaciones = 0
        vm.eliminarOfertaLocal(oferta) { confirmaciones++ }
        assertEquals(0, confirmaciones)
        assertEquals(listOf(oferta), vm.uiState.value.activas)
        assertNotNull(vm.uiState.value.errorLocal)
        assertFalse(vm.uiState.value.ocupado)

        fallar = false
        vm.eliminarOfertaLocal(oferta) { confirmaciones++ }

        assertEquals(1, confirmaciones)
        assertTrue(vm.uiState.value.activas.isEmpty())
        assertNull(vm.uiState.value.errorLocal)
    }
}
