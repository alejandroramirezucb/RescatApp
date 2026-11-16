package com.rescatapp.features.inicio.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.OfertaDaoFalso
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.OfertaEntity
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.inicio.domain.EditarOfertaUseCase
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

class OfertasNegocioLocalesViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val oferta = ofertasDeEjemplo.first().copy(comercio = UsuarioDemo.NEGOCIO_DEMO)
    private val local = crearRepositorioOfertas(listOf(oferta))
    private val remoto = FuenteRemotaFalsa(mutableListOf(oferta.copy(id = 101)))

    private fun crearViewModel() =
        OfertasNegocioViewModel(local, remoto, EditarOfertaUseCase(local))

    @Test
    fun guardarLocalEsperaLaEscrituraRoomAntesDeCerrarElDialogo() = runTest {
        val respuesta = CompletableDeferred<Unit>()
        val base = OfertaDaoFalso(listOf(oferta))
        val dao = object : OfertaDao by base {
            override suspend fun actualizar(oferta: OfertaEntity) {
                respuesta.await()
                base.actualizar(oferta)
            }
        }
        val repositorio = RepositorioOfertas(dao)
        val vm = OfertasNegocioViewModel(repositorio, remoto, EditarOfertaUseCase(repositorio))
        var guardadas = 0
        vm.actualizarOferta(oferta.copy(precioRescate = 1.0)) { guardadas++ }
        assertTrue(vm.uiState.value.guardandoLocal)
        assertEquals(0, guardadas)

        respuesta.complete(Unit)

        assertFalse(vm.uiState.value.guardandoLocal)
        assertEquals(1, guardadas)
        assertEquals(1.0, vm.uiState.value.activas.first().precioRescate, 0.001)
    }

    @Test
    fun unErrorRoomConservaElDialogoYLiberaElBloqueoLocal() = runTest {
        val dao = object : OfertaDao by OfertaDaoFalso(listOf(oferta)) {
            override suspend fun actualizar(oferta: OfertaEntity): Unit =
                throw IOException("Error de escritura")
        }
        val repositorio = RepositorioOfertas(dao)
        val vm = OfertasNegocioViewModel(repositorio, remoto, EditarOfertaUseCase(repositorio))
        var guardadas = 0
        vm.actualizarOferta(oferta) { guardadas++ }

        assertEquals(0, guardadas)
        assertFalse(vm.uiState.value.guardandoLocal)
        assertNotNull(vm.uiState.value.errorLocal)
        vm.limpiarMensaje()
        assertNull(vm.uiState.value.errorLocal)
    }
}
