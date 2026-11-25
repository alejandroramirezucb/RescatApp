package com.rescatapp.features.detalle.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioCarrito
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.core.util.suscripcionPantalla
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
    private val repositorioCarrito: RepositorioCarrito
) : ViewModel() {
    private val ofertaId: Int = savedStateHandle[ArgumentosRuta.OFERTA_ID] ?: OFERTA_INEXISTENTE
    private val mensaje = MutableStateFlow<String?>(null)

    val uiState: StateFlow<DetalleUiState> = combine(
        repositorioOfertas.obtenerPorId(ofertaId),
        repositorioCarrito.carrito,
        mensaje
    ) { oferta, carrito, mensaje ->
        DetalleUiState(
            oferta = oferta,
            enCarrito = carrito.cantidadDe(ofertaId),
            mensaje = mensaje
        )
    }.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = DetalleUiState()
    )

    fun agregarAlCarrito() {
        val oferta = uiState.value.oferta ?: return
        repositorioCarrito.agregar(oferta)
        mensaje.value = "Agregaste ${oferta.nombre} al carrito."
    }

    fun limpiarMensaje() {
        mensaje.value = null
    }
}
