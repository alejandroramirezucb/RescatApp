package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Pedido
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.detalle.domain.DetalleUiState
import com.rescatapp.features.detalle.domain.ReservarOfertaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repositorioOfertas: RepositorioOfertas,
    private val reservarOferta: ReservarOfertaUseCase
) : ViewModel() {
    private val ofertaId: Int = savedStateHandle["ofertaId"] ?: -1
    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DetalleUiState> = combine(
        repositorioOfertas.obtenerPorId(ofertaId),
        mensaje
    ) { oferta, mensajeActual ->
        DetalleUiState(
            oferta = oferta,
            noEncontrada = oferta == null,
            mensaje = mensajeActual
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetalleUiState(noEncontrada = ofertaId < 0)
    )

    fun reservar() {
        mensaje.value = when (val resultado = reservarOferta(ofertaId)) {
            is ResultadoOperacion.Exito ->
                "Reservaste ${(resultado.valor as Pedido).nombreOferta}"

            is ResultadoOperacion.Error -> resultado.mensaje
        }
    }

    fun limpiarMensaje() {
        mensaje.value = null
    }
}
