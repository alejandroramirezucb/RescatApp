package com.rescatapp.features.inicio.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.EstadoRemoto
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.inicio.domain.EditarOfertaUseCase
import java.io.IOException
import kotlinx.coroutines.CancellationException
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

class OfertasNegocioOperacionesRemotasTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val oferta = ofertasDeEjemplo.first().copy(comercio = UsuarioDemo.NEGOCIO_DEMO)
    private val local = crearRepositorioOfertas(listOf(oferta))
    private val remoto = FuenteRemotaFalsa(mutableListOf(oferta.copy(id = 101)))

    @Test
    fun publicarUsaElIdDelServidorYRecuperaTambienLasOfertasAnteriores() = runTest {
        val vm = crearViewModel()
        val respuesta = CompletableDeferred<Unit>()
        remoto.antesDeMutar = { respuesta.await() }
        vm.publicarEnLinea(oferta)
        assertEquals(TipoOperacionOferta.PUBLICAR, vm.uiState.value.operacionEnCurso?.tipo)
        vm.publicarEnLinea(oferta)
        vm.cargarOfertasRemotas()
        assertEquals(1, remoto.mutaciones)
        assertEquals(0, remoto.consultas)

        respuesta.complete(Unit)

        assertEquals(PestanaOfertas.EN_LINEA, vm.uiState.value.pestana)
        assertEquals(setOf(101, 999), ofertasEnLinea(vm).map { it.id }.toSet())
        assertEquals(listOf(oferta), local.ofertas.first())
        assertFalse(vm.uiState.value.ocupado)
        assertNotNull(vm.uiState.value.mensaje)
    }

    @Test
    fun editarYEliminarUsanElIdRemotoYActualizanSoloLaListaRemota() = runTest {
        val vm = crearViewModel()
        vm.cargarOfertasRemotas()
        val editada = remoto.ofertas.first().copy(precioRescate = 1.0)
        vm.editarOfertaRemota(editada)

        assertEquals(101, remoto.ultimoId)
        assertEquals(listOf(editada), ofertasEnLinea(vm))
        vm.eliminarOfertaRemota(editada)
        assertEquals(101, remoto.ultimoId)
        assertTrue(ofertasEnLinea(vm).isEmpty())
        assertEquals(listOf(oferta), local.ofertas.first())
    }

    @Test
    fun cadaMutacionFallidaConservaLosDatosLiberaElProgresoYPermiteReintentar() = runTest {
        for (tipo in TipoOperacionOferta.entries) {
            remoto.ofertas.clear()
            remoto.ofertas.add(oferta.copy(id = 101))
            remoto.antesDeMutar = {}
            val vm = crearViewModel()
            vm.cargarOfertasRemotas()
            val anteriores = ofertasEnLinea(vm)
            val respuesta = CompletableDeferred<Unit>()
            remoto.antesDeMutar = { respuesta.await() }

            when (tipo) {
                TipoOperacionOferta.PUBLICAR -> vm.publicarEnLinea(oferta)

                TipoOperacionOferta.EDITAR -> vm.editarOfertaRemota(
                    anteriores.first().copy(precioRescate = 1.0)
                )

                TipoOperacionOferta.ELIMINAR -> vm.eliminarOfertaRemota(anteriores.first())
            }
            assertEquals(tipo, vm.uiState.value.operacionEnCurso?.tipo)
            respuesta.completeExceptionally(IOException())

            assertEquals(anteriores, ofertasEnLinea(vm))
            assertFalse(vm.uiState.value.ocupado)
            assertNotNull(vm.uiState.value.errorOperacion)
            assertEquals(tipo, vm.uiState.value.operacionFallida?.tipo)
            assertEquals(listOf(oferta), local.ofertas.first())

            remoto.antesDeMutar = {}
            vm.reintentarOperacion()
            assertNull(vm.uiState.value.errorOperacion)
            assertNull(vm.uiState.value.operacionFallida)
            assertFalse(vm.uiState.value.ocupado)
            assertNotNull(vm.uiState.value.mensaje)
        }
    }

    @Test
    fun cancelacionNoSePresentaComoErrorNiDejaIndicadoresBloqueados() = runTest {
        val vm = crearViewModel()
        remoto.consultar = { throw CancellationException() }
        vm.cargarOfertasRemotas()
        assertFalse(vm.uiState.value.ocupado)
        assertTrue(vm.uiState.value.estadoRemoto is EstadoRemoto.Exito)

        remoto.antesDeMutar = { throw CancellationException() }
        vm.publicarEnLinea(oferta)
        assertFalse(vm.uiState.value.ocupado)
        assertNull(vm.uiState.value.errorOperacion)
    }

    @Test
    fun unGetFallidoDespuesDelPostNoInvitaARepetirUnaPublicacionYaConfirmada() = runTest {
        remoto.consultar = { throw IOException() }
        val vm = crearViewModel()
        vm.publicarEnLinea(oferta)

        assertTrue(vm.uiState.value.estadoRemoto is EstadoRemoto.Error)
        assertNull(vm.uiState.value.operacionFallida)
        assertNotNull(vm.uiState.value.mensaje)
        remoto.consultar = null
        vm.cargarOfertasRemotas()
        assertTrue(ofertasEnLinea(vm).any { it.id == 999 })
        assertEquals(1, remoto.mutaciones)
    }

    private fun crearViewModel() =
        OfertasNegocioViewModel(local, remoto, EditarOfertaUseCase(local))
}
