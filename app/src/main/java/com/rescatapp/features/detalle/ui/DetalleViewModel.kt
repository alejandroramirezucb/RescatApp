package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import com.rescatapp.features.pago.domain.PasarelaPagoSimulada
import com.rescatapp.features.pago.model.ComprobantePago
import com.rescatapp.features.pago.model.DatosQrPago
import com.rescatapp.features.pago.model.EstadoPago
import com.rescatapp.features.pago.model.MetodoPago
import com.rescatapp.features.pago.model.ResultadoPago
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val OFERTA_INEXISTENTE = -1

private data class EstadoPagoInterno(
    val mostrandoPasarela: Boolean = false,
    val datosQr: DatosQrPago? = null,
    val comprobante: ComprobantePago? = null,
    val estado: EstadoPago = EstadoPago.PENDIENTE,
    val metodoSeleccionado: MetodoPago = MetodoPago.QR_SIMPLE,
    val error: String? = null
)

@HiltViewModel
class DetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repositorioOfertas: RepositorioOfertas,
    private val reservarOferta: ReservarOfertaUseCase,
    private val pasarelaPago: PasarelaPagoSimulada = PasarelaPagoSimulada()
) : ViewModel() {
    private val ofertaId: Int = savedStateHandle[ArgumentosRuta.OFERTA_ID] ?: OFERTA_INEXISTENTE
    private val mensaje = MutableStateFlow<String?>(null)
    private val estadoPago = MutableStateFlow(EstadoPagoInterno())
    private var reservaEnCurso = false

    val uiState: StateFlow<DetalleUiState> = combine(
        repositorioOfertas.obtenerPorId(ofertaId),
        mensaje,
        estadoPago
    ) { oferta, msg, pago ->
        DetalleUiState(
            oferta = oferta,
            mensaje = msg,
            mostrandoPasarelaPago = pago.mostrandoPasarela,
            datosQrPago = pago.datosQr,
            comprobantePago = pago.comprobante,
            estadoPago = pago.estado,
            metodoPagoSeleccionado = pago.metodoSeleccionado,
            errorPago = pago.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = DetalleUiState()
    )

    fun abrirPasarelaPago() {
        estadoPago.value = EstadoPagoInterno(
            mostrandoPasarela = true,
            estado = EstadoPago.PENDIENTE,
            metodoSeleccionado = MetodoPago.QR_SIMPLE
        )
    }

    fun cerrarPasarelaPago() {
        estadoPago.update { it.copy(mostrandoPasarela = false) }
    }

    fun seleccionarMetodoPago(metodo: MetodoPago) {
        estadoPago.update { it.copy(metodoSeleccionado = metodo, datosQr = null, error = null) }
    }

    fun generarQrPago() {
        val oferta = uiState.value.oferta ?: return
        val nuevoQr = pasarelaPago.generarQrParaOferta(oferta)
        estadoPago.update {
            it.copy(
                datosQr = nuevoQr,
                estado = EstadoPago.PENDIENTE,
                error = null
            )
        }
    }

    fun simularPagoQr(simularExito: Boolean = true) {
        val qrActual = estadoPago.value.datosQr ?: return
        estadoPago.update { it.copy(estado = EstadoPago.PROCESANDO, error = null) }

        viewModelScope.launch {
            when (val resultado = pasarelaPago.procesarPagoQr(qrActual, simularExito)) {
                is ResultadoPago.Exito -> {
                    estadoPago.update {
                        it.copy(
                            estado = EstadoPago.APROBADO,
                            comprobante = resultado.comprobante
                        )
                    }
                }

                is ResultadoPago.Rechazado -> {
                    estadoPago.update {
                        it.copy(
                            estado = EstadoPago.RECHAZADO,
                            error = resultado.motivo
                        )
                    }
                }
            }
        }
    }

    fun confirmarPagoEfectivo() {
        val oferta = uiState.value.oferta ?: return
        viewModelScope.launch {
            when (val resultado = pasarelaPago.procesarPagoEfectivo(oferta)) {
                is ResultadoPago.Exito -> {
                    estadoPago.update { it.copy(mostrandoPasarela = false) }
                    ejecutarReserva(oferta)
                }

                is ResultadoPago.Rechazado -> {
                    estadoPago.update {
                        it.copy(estado = EstadoPago.RECHAZADO, error = resultado.motivo)
                    }
                }
            }
        }
    }

    fun completarReservaTrasPagoAprobado() {
        val oferta = uiState.value.oferta ?: return
        estadoPago.update { it.copy(mostrandoPasarela = false) }
        ejecutarReserva(oferta)
    }

    fun reservar() {
        val oferta = uiState.value.oferta ?: return
        ejecutarReserva(oferta)
    }

    private fun ejecutarReserva(oferta: Oferta) {
        if (reservaEnCurso) return
        reservaEnCurso = true
        viewModelScope.launch {
            try {
                mensaje.value = when (val resultado = reservarOferta(ofertaId)) {
                    is ResultadoOperacion.Exito ->
                        "Reservaste ${oferta.nombre}. Puedes verlo en Pedidos."

                    is ResultadoOperacion.Error -> resultado.mensaje
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                mensaje.value = "No se pudo guardar la reserva. Intenta nuevamente."
            } finally {
                reservaEnCurso = false
            }
        }
    }

    fun limpiarMensaje() {
        mensaje.value = null
    }
}
