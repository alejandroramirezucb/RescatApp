package com.rescatapp.features.registro.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.OfertaDaoFalso
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.OfertaEntity
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.MensajesValidacion
import com.rescatapp.features.registro.domain.RegistrarOfertaUseCase
import com.rescatapp.features.registro.domain.ValidarOfertaUseCase
import com.rescatapp.features.registro.domain.camposValidos
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
    private val viewModel = RegistroViewModel(
        validarOferta = ValidarOfertaUseCase(),
        registrarOferta = RegistrarOfertaUseCase(repositorioOfertas)
    )

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
        val dao = OfertaDaoFalso()
        val permitirEscritura = CompletableDeferred<Unit>()
        val daoLento = object : OfertaDao by dao {
            override suspend fun insertar(oferta: OfertaEntity): Long {
                permitirEscritura.await()
                return dao.insertar(oferta)
            }
        }
        val repo = RepositorioOfertas(daoLento)
        val registro = RegistroViewModel(ValidarOfertaUseCase(), RegistrarOfertaUseCase(repo))
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
        val dao = OfertaDaoFalso()
        var falla = true
        val daoConError = object : OfertaDao by dao {
            override suspend fun insertar(oferta: OfertaEntity): Long {
                if (falla) error("Error de escritura")
                return dao.insertar(oferta)
            }
        }
        val repo = RepositorioOfertas(daoConError)
        val registro = RegistroViewModel(ValidarOfertaUseCase(), RegistrarOfertaUseCase(repo))
        registro.onCampoCambiado(camposValidos)

        registro.publicar()

        assertFalse(registro.uiState.value.guardando)
        assertFalse(registro.uiState.value.guardadoExitoso)
        assertEquals("No se pudo guardar la oferta. Intenta nuevamente.", registro.uiState.value.mensaje)
        assertEquals(14, repo.ofertas.first().size)

        falla = false
        registro.publicar()

        assertTrue(registro.uiState.value.guardadoExitoso)
        assertNull(registro.uiState.value.mensaje)
        assertEquals(15, repo.ofertas.first().size)
    }
}
