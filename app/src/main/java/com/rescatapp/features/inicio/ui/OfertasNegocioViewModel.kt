package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.util.suscripcionPantalla
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class OfertasNegocioViewModel @Inject constructor(
    private val repositorioOfertas: RepositorioOfertas
) : ViewModel() {
    val ofertas: StateFlow<List<Oferta>> = repositorioOfertas.ofertas.stateIn(
        scope = viewModelScope,
        started = suscripcionPantalla,
        initialValue = emptyList()
    )
    private val error = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = error.asStateFlow()
    private var guardando = false

    fun actualizarOferta(oferta: Oferta, onGuardada: () -> Unit = {}) {
        if (guardando) return
        guardando = true
        error.value = null
        viewModelScope.launch {
            try {
                repositorioOfertas.actualizar(oferta)
                onGuardada()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                error.value = "No se pudieron guardar los cambios. Intenta nuevamente."
            } finally {
                guardando = false
            }
        }
    }

    fun limpiarMensaje() {
        error.value = null
    }
}
