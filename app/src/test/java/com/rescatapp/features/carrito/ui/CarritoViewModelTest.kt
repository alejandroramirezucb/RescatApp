package com.rescatapp.features.carrito.ui

import com.rescatapp.ReglaDispatcherPrincipal
import com.rescatapp.core.data.RepositorioCarrito
import com.rescatapp.core.data.SupabaseApiFalsa
import com.rescatapp.core.data.crearRepositorioOfertas
import com.rescatapp.core.data.crearRepositorioPedidos
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.features.carrito.domain.ConfirmarCarritoUseCase
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

class CarritoViewModelTest {
    @get:Rule
    val reglaDispatcher = ReglaDispatcherPrincipal()

    private val api = SupabaseApiFalsa()
    private val repositorioCarrito = RepositorioCarrito()
    private val viewModel by lazy {
        CarritoViewModel(
            repositorioCarrito = repositorioCarrito,
            confirmarCarrito = ConfirmarCarritoUseCase(
                repositorioCarrito,
                crearRepositorioOfertas(api = api),
                crearRepositorioPedidos(api = api)
            ),
            pasarelaPago = PasarelaPagoSimulada(esperaMs = 0L)
        )
    }

    @Test
    fun confirmarReservaTodoCierraLaPasarelaYMuestraUnMensaje() = runTest {
        observarDuranteLaPrueba(viewModel.uiState)
        repositorioCarrito.cambiarCantidad(ofertasDeEjemplo.first { it.id == 13 }, 2)
        viewModel.abrirPasarelaPago()

        viewModel.confirmar()

        val estado = viewModel.uiState.value
        assertFalse(estado.pasarela.visible)
        assertTrue(estado.carrito.items.isEmpty())
        assertEquals("Reservaste 2 productos. Puedes verlos en Pedidos.", estado.mensaje)
    }

    @Test
    fun limpiarMensajeLoOculta() = runTest {
        observarDuranteLaPrueba(viewModel.uiState)
        repositorioCarrito.agregar(ofertasDeEjemplo.first { it.id == 13 })
        viewModel.confirmar()

        viewModel.limpiarMensaje()

        assertNull(viewModel.uiState.value.mensaje)
    }

    @Test
    fun abrirPasarelaPagoIniciaPendienteConQrSimple() = runTest {
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
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()

        viewModel.seleccionarMetodoPago(MetodoPago.EFECTIVO)

        assertEquals(MetodoPago.EFECTIVO, viewModel.uiState.value.pasarela.metodo)
    }

    @Test
    fun generarQrPagoUsaElTotalDelCarrito() = runTest {
        observarDuranteLaPrueba(viewModel.uiState)
        repositorioCarrito.cambiarCantidad(ofertasDeEjemplo.first { it.id == 14 }, 2)
        viewModel.abrirPasarelaPago()

        viewModel.generarQrPago()

        assertEquals(50.0, viewModel.uiState.value.pasarela.datosQr?.monto ?: 0.0, 0.001)
    }

    @Test
    fun simularPagoQrAprobadoGeneraComprobante() = runTest {
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
        observarDuranteLaPrueba(viewModel.uiState)
        viewModel.abrirPasarelaPago()
        viewModel.generarQrPago()

        viewModel.simularPagoQr(aprobado = false)

        val pasarela = viewModel.uiState.value.pasarela
        assertEquals(EstadoPago.RECHAZADO, pasarela.estado)
        assertNotNull(pasarela.error)
    }
}
