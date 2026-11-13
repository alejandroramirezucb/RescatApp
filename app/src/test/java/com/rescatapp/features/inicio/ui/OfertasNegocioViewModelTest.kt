package com.rescatapp.features.inicio.ui

import com.google.gson.JsonSyntaxException
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.OfertaDaoFalso
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.OfertaEntity
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.data.remote.FuenteOfertasRemotas
import com.rescatapp.core.model.EstadoRemoto
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.inicio.domain.EditarOfertaUseCase
import java.io.EOFException
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class OfertasNegocioViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val oferta = ofertasDeEjemplo.first().copy(comercio = UsuarioDemo.NEGOCIO_DEMO)
    private val local = crearRepositorioOfertas(listOf(oferta))
    private val remoto = FuenteRemotaFalsa(mutableListOf(oferta.copy(id = 101)))

    @Test
    fun entrarEnLineaEmiteCargandoYDespuesExitoSinRepetirLaSolicitud() = runTest {
        val respuesta = CompletableDeferred<List<Oferta>>()
        remoto.consultar = { respuesta.await() }
        val vm = crearViewModel()
        assertEquals(0, remoto.consultas)

        vm.seleccionarPestana(PestanaOfertas.EN_LINEA)
        assertEquals(EstadoRemoto.Cargando, vm.uiState.value.estadoRemoto)
        vm.seleccionarPestana(PestanaOfertas.EN_LINEA)
        vm.cargarOfertasRemotas()
        assertEquals(1, remoto.consultas)

        respuesta.complete(remoto.ofertas.toList())
        assertEquals(EstadoRemoto.Exito(remoto.ofertas.toList()), vm.uiState.value.estadoRemoto)
        assertFalse(vm.uiState.value.ocupado)
    }

    @Test
    fun erroresDeRedServidorYConversionTerminanLaCargaConMensajesComprensibles() = runTest {
        val fallos = listOf(
            IOException() to "conexión",
            HttpException(Response.error<Unit>(500, "fallo".toResponseBody())) to "servidor",
            JsonSyntaxException("json inválido") to "interpretar",
            EOFException("respuesta incompleta") to "interpretar",
            IllegalArgumentException("categoría inválida") to "interpretar",
            IllegalStateException("fallo inesperado") to "completar"
        )
        for ((fallo, mensaje) in fallos) {
            val respuesta = CompletableDeferred<List<Oferta>>()
            remoto.consultar = { respuesta.await() }
            val vm = crearViewModel()
            vm.cargarOfertasRemotas()
            assertEquals(EstadoRemoto.Cargando, vm.uiState.value.estadoRemoto)

            respuesta.completeExceptionally(fallo)

            val error = vm.uiState.value.estadoRemoto as EstadoRemoto.Error
            assertTrue(error.mensaje.contains(mensaje))
            assertFalse(vm.uiState.value.ocupado)
        }
    }

    @Test
    fun reintentarConsultaRecuperaElListadoYUnaRespuestaVaciaEsExito() = runTest {
        remoto.consultar = { throw IOException() }
        val vm = crearViewModel()
        vm.cargarOfertasRemotas()
        assertTrue(vm.uiState.value.estadoRemoto is EstadoRemoto.Error)

        remoto.consultar = { emptyList() }
        vm.cargarOfertasRemotas()

        assertEquals(EstadoRemoto.Exito(emptyList<Oferta>()), vm.uiState.value.estadoRemoto)
        assertEquals(2, remoto.consultas)
    }

    @Test
    fun volverAEntrarConsultaDeNuevoYFiltraElNegocioSinAlterarLosLocales() = runTest {
        remoto.ofertas.add(oferta.copy(id = 102, comercio = "Otro negocio"))
        val vm = crearViewModel()
        vm.seleccionarPestana(PestanaOfertas.EN_LINEA)
        assertEquals(listOf(remoto.ofertas.first()), ofertasEnLinea(vm))
        vm.seleccionarPestana(PestanaOfertas.ACTIVAS)
        vm.seleccionarPestana(PestanaOfertas.EN_LINEA)

        assertEquals(2, remoto.consultas)
        assertEquals(listOf(oferta), local.ofertas.first())
        assertEquals(listOf(oferta), vm.uiState.value.activas)
    }

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

    private fun ofertasEnLinea(vm: OfertasNegocioViewModel): List<Oferta> =
        (vm.uiState.value.estadoRemoto as EstadoRemoto.Exito).datos

    private class FuenteRemotaFalsa(val ofertas: MutableList<Oferta>) : FuenteOfertasRemotas {
        var consultar: (suspend () -> List<Oferta>)? = null
        var antesDeMutar: suspend () -> Unit = {}
        var consultas = 0
        var mutaciones = 0
        var ultimoId: Int? = null

        override suspend fun obtenerOfertasRemotas(): List<Oferta> {
            consultas++
            return consultar?.invoke() ?: ofertas.toList()
        }

        override suspend fun crearOferta(oferta: Oferta): Oferta {
            mutaciones++
            antesDeMutar()
            return oferta.copy(id = 999).also { ofertas.add(it) }
        }

        override suspend fun editarOferta(id: Int, ofertaActualizada: Oferta): Oferta {
            mutaciones++
            ultimoId = id
            antesDeMutar()
            ofertas.replaceAll { if (it.id == id) ofertaActualizada else it }
            return ofertaActualizada
        }

        override suspend fun eliminarOferta(id: Int) {
            mutaciones++
            ultimoId = id
            antesDeMutar()
            ofertas.removeAll { it.id == id }
        }
    }
}
