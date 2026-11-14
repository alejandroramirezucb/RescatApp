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
    private val pasarelaPagoInstantanea = PasarelaPagoSimulada(tiempoEsperaMs = 0L)

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
    fun reservarActualizaLaCantidadYMuestraUnMensaje() = runTest {
        val viewModel = crearViewModel(ofertaId = 13)
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.reservar()

        val estado = viewModel.uiState.value
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
    fun abrirPasarelaPagoInicializaEstadoEnPendienteConQrSimple() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)

        viewModel.abrirPasarelaPago()

        val estado = viewModel.uiState.value
        assertTrue(estado.mostrandoPasarelaPago)
        assertEquals(EstadoPago.PENDIENTE, estado.estadoPago)
        assertEquals(MetodoPago.QR_SIMPLE, estado.metodoPagoSeleccionado)
        assertNull(estado.datosQrPago)
        assertNull(estado.errorPago)
    }

    @Test
    fun seleccionarMetodoPagoCambiaElMetodoSeleccionado() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()

        viewModel.seleccionarMetodoPago(MetodoPago.EFECTIVO)
        assertEquals(MetodoPago.EFECTIVO, viewModel.uiState.value.metodoPagoSeleccionado)

        viewModel.seleccionarMetodoPago(MetodoPago.QR_SIMPLE)
        assertEquals(MetodoPago.QR_SIMPLE, viewModel.uiState.value.metodoPagoSeleccionado)
    }

    @Test
    fun generarQrPagoCreaDatosDePagoConMontoDeLaOferta() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()

        viewModel.generarQrPago()

        val datosQr = viewModel.uiState.value.datosQrPago
        assertNotNull(datosQr)
        assertEquals(25.0, datosQr?.monto ?: 0.0, 0.001)
        assertEquals(EstadoPago.PENDIENTE, viewModel.uiState.value.estadoPago)
    }

    @Test
    fun simularPagoQrExitosoActualizaAprobadoYComprobante() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()
        viewModel.generarQrPago()

        viewModel.simularPagoQr(simularExito = true)

        val estado = viewModel.uiState.value
        assertEquals(EstadoPago.APROBADO, estado.estadoPago)
        assertNotNull(estado.comprobantePago)
        assertEquals(MetodoPago.QR_SIMPLE, estado.comprobantePago?.metodoPago)
    }

    @Test
    fun simularPagoQrRechazadoActualizaEstadoRechazadoConError() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()
        viewModel.generarQrPago()

        viewModel.simularPagoQr(simularExito = false)

        val estado = viewModel.uiState.value
        assertEquals(EstadoPago.RECHAZADO, estado.estadoPago)
        assertNotNull(estado.errorPago)
    }

    @Test
    fun completarReservaTrasPagoAprobadoCierraPasarelaYGeneraReserva() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()
        viewModel.generarQrPago()
        viewModel.simularPagoQr(simularExito = true)

        viewModel.completarReservaTrasPagoAprobado()

        val estado = viewModel.uiState.value
        assertFalse(estado.mostrandoPasarelaPago)
        assertEquals("Reservaste Pack Sorpresa. Puedes verlo en Pedidos.", estado.mensaje)
    }

    @Test
    fun confirmarPagoEfectivoCierraPasarelaYGeneraReserva() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()
        viewModel.seleccionarMetodoPago(MetodoPago.EFECTIVO)

        viewModel.confirmarPagoEfectivo()

        val estado = viewModel.uiState.value
        assertFalse(estado.mostrandoPasarelaPago)
        assertEquals("Reservaste Pack Sorpresa. Puedes verlo en Pedidos.", estado.mensaje)
    }

    @Test
    fun cerrarPasarelaPagoOcultaElDialogo() = runTest {
        val viewModel = crearViewModel(ofertaId = 14)
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()
        assertTrue(viewModel.uiState.value.mostrandoPasarelaPago)

        viewModel.cerrarPasarelaPago()
        assertFalse(viewModel.uiState.value.mostrandoPasarelaPago)
    }

    private fun crearViewModel(ofertaId: Int) = DetalleViewModel(
        savedStateHandle = SavedStateHandle(mapOf(ArgumentosRuta.OFERTA_ID to ofertaId)),
        repositorioOfertas = repositorioOfertas,
        reservarOferta = ReservarOfertaUseCase(
            repositorioOfertas,
            RepositorioPedidos(FakePedidoDao())
        ),
        pasarelaPago = pasarelaPagoInstantanea
    )
}
