package com.rescatapp.features.carrito.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioCarrito
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.carrito.domain.ConfirmarCarritoUseCase
import com.rescatapp.features.pago.domain.PasarelaPagoSimulada
import com.rescatapp.features.pago.model.ComprobantePago
import com.rescatapp.features.pago.model.EstadoPago
import com.rescatapp.features.pago.model.EstadoPasarela
import com.rescatapp.features.pago.model.MetodoPago
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CarritoViewModel @Inject constructor(
    private val repositorioCarrito: RepositorioCarrito,
    private val confirmarCarrito: ConfirmarCarritoUseCase,
    private val pasarelaPago: PasarelaPagoSimulada
) : ViewModel() {
    private val mensaje = MutableStateFlow<String?>(null)
    private val pasarela = MutableStateFlow(EstadoPasarela())
    private val confirmando = MutableStateFlow(false)

    val uiState: StateFlow<CarritoUiState> = combine(
        repositorioCarrito.carrito,
        mensaje,
        pasarela,
        confirmando
    ) { carrito, mensaje, pasarela, confirmando ->
        CarritoUiState(carrito, mensaje, pasarela, confirmando)
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = CarritoUiState()
    )

    fun cambiarCantidad(oferta: Oferta, cantidad: Int) {
        repositorioCarrito.cambiarCantidad(oferta, cantidad)
    }

    fun eliminar(ofertaId: Int) {
        repositorioCarrito.eliminar(ofertaId)
    }

    fun abrirPasarelaPago() {
        pasarela.value = EstadoPasarela(visible = true)
    }

    fun cerrarPasarelaPago() {
        pasarela.update { it.copy(visible = false) }
    }

    fun seleccionarMetodoPago(metodo: MetodoPago) {
        pasarela.update { it.copy(metodo = metodo, datosQr = null, error = null) }
    }

    fun generarQrPago() {
        val carrito = repositorioCarrito.carrito.value
        pasarela.update {
            it.copy(
                datosQr = pasarelaPago.generarQr(carrito.total, carrito.comercios),
                estado = EstadoPago.PENDIENTE,
                error = null
            )
        }
    }

    fun simularPagoQr(aprobado: Boolean) {
        val datosQr = pasarela.value.datosQr ?: return
        pasarela.update { it.copy(estado = EstadoPago.PROCESANDO, error = null) }
        viewModelScope.launch {
            when (val resultado = pasarelaPago.procesarPagoQr(datosQr, aprobado)) {
                is ResultadoOperacion.Exito -> pasarela.update {
                    it.copy(
                        estado = EstadoPago.APROBADO,
                        comprobante = resultado.valor as ComprobantePago
                    )
                }

                is ResultadoOperacion.Error -> pasarela.update {
                    it.copy(estado = EstadoPago.RECHAZADO, error = resultado.mensaje)
                }
            }
        }
    }

    fun confirmar() {
        if (confirmando.value) return
        confirmando.value = true
        cerrarPasarelaPago()
        viewModelScope.launch {
            try {
                mensaje.value = when (val resultado = confirmarCarrito()) {
                    is ResultadoOperacion.Exito ->
                        "Reservaste ${resultado.valor} productos. Puedes verlos en Pedidos."

                    is ResultadoOperacion.Error -> resultado.mensaje
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                mensaje.value = "No se pudo guardar la reserva. Intenta nuevamente."
            } finally {
                confirmando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        mensaje.value = null
    }
}
