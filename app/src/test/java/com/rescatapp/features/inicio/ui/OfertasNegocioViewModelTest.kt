package com.rescatapp.features.inicio.ui

import com.google.gson.JsonSyntaxException
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.model.EstadoRemoto
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.inicio.domain.EditarOfertaUseCase
import java.io.EOFException
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    private fun crearViewModel() =
        OfertasNegocioViewModel(local, remoto, EditarOfertaUseCase(local))

    @Test
    fun eliminaTambienLasOfertasFinalizadas() = runTest {
        local.actualizar(oferta.copy(cantidadDisponible = 0))
        val vm = crearViewModel()
        assertEquals(1, vm.uiState.value.finalizadas.size)

        vm.eliminarOfertaLocal(vm.uiState.value.finalizadas.single())

        assertTrue(vm.uiState.value.finalizadas.isEmpty())
        assertTrue(local.ofertas.first().isEmpty())
    }
}
