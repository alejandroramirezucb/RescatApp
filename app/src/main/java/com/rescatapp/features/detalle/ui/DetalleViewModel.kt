package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

private const val OFERTA_INEXISTENTE = -1

@HiltViewModel
class DetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repositorioOfertas: RepositorioOfertas,
    private val reservarOferta: ReservarOfertaUseCase
) : ViewModel() {
    private val ofertaId: Int = savedStateHandle[ArgumentosRuta.OFERTA_ID] ?: OFERTA_INEXISTENTE
    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DetalleUiState> =
        combine(repositorioOfertas.obtenerPorId(ofertaId), mensaje, ::DetalleUiState).stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = DetalleUiState(oferta = repositorioOfertas.buscarPorId(ofertaId))
        )

    fun reservar() {
        val nombreOferta = uiState.value.oferta?.nombre ?: return
        mensaje.value = when (val resultado = reservarOferta(ofertaId)) {
            is ResultadoOperacion.Exito -> "Reservaste $nombreOferta. Puedes verlo en Pedidos."
            is ResultadoOperacion.Error -> resultado.mensaje
        }
    }

    fun limpiarMensaje() {
        mensaje.value = null
    }
}
