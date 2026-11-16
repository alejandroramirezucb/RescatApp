package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
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

private const val OFERTA_INEXISTENTE = -1

@HiltViewModel
class DetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repositorioOfertas: RepositorioOfertas,
    private val reservarOferta: ReservarOfertaUseCase,
    private val pasarelaPago: PasarelaPagoSimulada
) : ViewModel() {
    private val ofertaId: Int = savedStateHandle[ArgumentosRuta.OFERTA_ID] ?: OFERTA_INEXISTENTE
    private val mensaje = MutableStateFlow<String?>(null)
    private val pasarela = MutableStateFlow(EstadoPasarela())
    private var reservaEnCurso = false

    val uiState: StateFlow<DetalleUiState> = combine(
        repositorioOfertas.obtenerPorId(ofertaId),
        mensaje,
        pasarela
    ) { oferta, mensaje, pasarela ->
        DetalleUiState(oferta = oferta, mensaje = mensaje, pasarela = pasarela)
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = DetalleUiState()
    )

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
        val oferta = uiState.value.oferta ?: return
        pasarela.update {
            it.copy(
                datosQr = pasarelaPago.generarQrParaOferta(oferta),
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

    fun reservar() {
        val oferta = uiState.value.oferta ?: return
        if (reservaEnCurso) return
        reservaEnCurso = true
        cerrarPasarelaPago()
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
