package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.local.FakePedidoDao
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import com.rescatapp.features.pago.domain.PasarelaPagoSimulada
import com.rescatapp.features.pago.model.EstadoPago
import com.rescatapp.features.pago.model.MetodoPago
import com.rescatapp.observarDuranteLaPrueba
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetalleViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val repositorioOfertas = crearRepositorioOfertas()

    @Test
    fun muestraLaOfertaRecibidaEnLaRuta() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)

        assertEquals("Pack Sorpresa", viewModel.uiState.value.oferta?.nombre)
    }

    @Test
    fun indicaCuandoLaOfertaNoExiste() = runTest {
        val viewModel = crearViewModel(ofertaId = 99)
        observarDuranteLaPrueba(viewModel.uiState)

        assertTrue(viewModel.uiState.value.noEncontrada)
    }

    @Test
    fun reservarCierraLaPasarelaActualizaLaCantidadYMuestraUnMensaje() = runTest {
        val viewModel = crearViewModel(ofertaId = 13)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()

        viewModel.reservar()

        val estado = viewModel.uiState.value
        assertFalse(estado.pasarela.visible)
        assertEquals(2, estado.oferta?.cantidadDisponible)
        assertEquals("Reservaste Pack Croissants. Puedes verlo en Pedidos.", estado.mensaje)
    }

    @Test
    fun limpiarMensajeLoOculta() = runTest {
        val viewModel = crearViewModel(ofertaId = 13)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.reservar()

        viewModel.limpiarMensaje()

        assertNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun abrirPasarelaPagoIniciaPendienteConQrSimple() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.abrirPasarelaPago()

        val pasarela = viewModel.uiState.value.pasarela
        assertTrue(pasarela.visible)
        assertEquals(EstadoPago.PENDIENTE, pasarela.estado)
        assertEquals(MetodoPago.QR_SIMPLE, pasarela.metodo)
        assertNull(pasarela.datosQr)
    }

    @Test
    fun seleccionarMetodoPagoCambiaElMetodo() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()

        viewModel.seleccionarMetodoPago(MetodoPago.EFECTIVO)

        assertEquals(MetodoPago.EFECTIVO, viewModel.uiState.value.pasarela.metodo)
    }

    @Test
    fun generarQrPagoUsaElMontoDeLaOferta() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()

        viewModel.generarQrPago()

        assertEquals(25.0, viewModel.uiState.value.pasarela.datosQr?.monto ?: 0.0, 0.001)
    }

    @Test
    fun simularPagoQrAprobadoGeneraComprobante() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()
        viewModel.generarQrPago()

        viewModel.simularPagoQr(aprobado = true)

        val pasarela = viewModel.uiState.value.pasarela
        assertEquals(EstadoPago.APROBADO, pasarela.estado)
        assertNotNull(pasarela.comprobante)
    }

    @Test
    fun simularPagoQrRechazadoMuestraError() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()
        viewModel.generarQrPago()

        viewModel.simularPagoQr(aprobado = false)

        val pasarela = viewModel.uiState.value.pasarela
        assertEquals(EstadoPago.RECHAZADO, pasarela.estado)
        assertNotNull(pasarela.error)
    }

    private fun crearViewModel(ofertaId: Int) = DetalleViewModel(
        savedStateHandle = SavedStateHandle(mapOf(ArgumentosRuta.OFERTA_ID to ofertaId)),
        repositorioOfertas = repositorioOfertas,
        reservarOferta = ReservarOfertaUseCase(
            repositorioOfertas,
            RepositorioPedidos(FakePedidoDao())
        ),
        pasarelaPago = PasarelaPagoSimulada(esperaMs = 0L)
    )
}
