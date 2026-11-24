package com.rescatapp.features.registro.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearAutenticacion
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.MensajesValidacion
import com.rescatapp.features.registro.domain.RegistrarOfertaUseCase
import com.rescatapp.features.registro.domain.ValidarOfertaUseCase
import com.rescatapp.features.registro.domain.camposValidos
import java.io.IOException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegistroViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioOfertas = crearRepositorioOfertas()
    private val viewModel by lazy {
        RegistroViewModel(
            validarOferta = ValidarOfertaUseCase(),
            registrarOferta = RegistrarOfertaUseCase(repositorioOfertas),
            repositorioAutenticacion = crearAutenticacion()
        )
    }

    @Test
    fun publicarDatosValidosGuardaLaOferta() = runTest {
        viewModel.onCampoCambiado(camposValidos)

        viewModel.publicar()

        assertTrue(viewModel.uiState.value.guardadoExitoso)
        assertEquals(15, repositorioOfertas.ofertas.first().size)
    }

    @Test
    fun publicarDatosInvalidosMuestraErroresSinGuardar() = runTest {
        viewModel.publicar()

        val estado = viewModel.uiState.value
        assertFalse(estado.guardadoExitoso)
        assertEquals(MensajesValidacion.NOMBRE_VACIO, estado.errores.nombre)
        assertEquals(14, repositorioOfertas.ofertas.first().size)
    }

    @Test
    fun corregirUnCampoQuitaSuErrorYMantieneLosDemas() = runTest {
        viewModel.publicar()

        viewModel.onCampoCambiado(CamposRegistro(nombre = "Pack Salteñas"))

        val errores = viewModel.uiState.value.errores
        assertNull(errores.nombre)
        assertEquals(MensajesValidacion.COMERCIO_VACIO, errores.comercio)
    }

    @Test
    fun esperaLaEscrituraYEvitaPublicarDosVecesMientrasGuarda() = runTest {
        val api = SupabaseApiFalsa()
        val permitirEscritura = CompletableDeferred<Unit>()
        api.esperaCreacion = permitirEscritura
        val repo = crearRepositorioOfertas(api = api)
        val registro =
            RegistroViewModel(
                ValidarOfertaUseCase(),
                RegistrarOfertaUseCase(repo),
                crearAutenticacion(api)
            )
        registro.onCampoCambiado(camposValidos)

        registro.publicar()
        registro.publicar()

        assertTrue(registro.uiState.value.guardando)
        assertFalse(registro.uiState.value.guardadoExitoso)
        assertEquals(14, repo.ofertas.first().size)

        permitirEscritura.complete(Unit)

        assertFalse(registro.uiState.value.guardando)
        assertTrue(registro.uiState.value.guardadoExitoso)
        assertEquals(15, repo.ofertas.first().size)
    }

    @Test
    fun unErrorDeRoomPermiteReintentarSinInformarUnGuardadoExitoso() = runTest {
        val api = SupabaseApiFalsa()
        api.fallo = IllegalStateException("Error de escritura")
        val repo = crearRepositorioOfertas(api = api)
        val registro =
            RegistroViewModel(
                ValidarOfertaUseCase(),
                RegistrarOfertaUseCase(repo),
                crearAutenticacion(api)
            )
        registro.onCampoCambiado(camposValidos)

        registro.publicar()

        assertFalse(registro.uiState.value.guardando)
        assertFalse(registro.uiState.value.guardadoExitoso)
        assertEquals(
            "No se pudo guardar la oferta. Intenta nuevamente.",
            registro.uiState.value.mensaje
        )
        assertEquals(14, repo.ofertas.first().size)

        api.fallo = null
        registro.publicar()

        assertTrue(registro.uiState.value.guardadoExitoso)
        assertNull(registro.uiState.value.mensaje)
        assertEquals(15, repo.ofertas.first().size)
    }

    @Test
    fun unErrorDeRedMuestraMensajeDeConexion() = runTest {
        val api = SupabaseApiFalsa()
        api.fallo = IOException("Sin internet")
        val repo = crearRepositorioOfertas(api = api)
        val registro = RegistroViewModel(
            ValidarOfertaUseCase(),
            RegistrarOfertaUseCase(repo),
            crearAutenticacion(api)
        )
        registro.onCampoCambiado(camposValidos)

        registro.publicar()

        assertFalse(registro.uiState.value.guardadoExitoso)
        assertEquals(
            "No pudimos conectarnos. Revisa tu conexión e intenta nuevamente.",
            registro.uiState.value.mensaje
        )
    }
}
