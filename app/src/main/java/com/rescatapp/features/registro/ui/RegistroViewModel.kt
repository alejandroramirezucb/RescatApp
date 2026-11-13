package com.rescatapp.features.registro.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.RegistrarOfertaUseCase
import com.rescatapp.features.registro.domain.ValidarOfertaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val validarOferta: ValidarOfertaUseCase,
    private val registrarOferta: RegistrarOfertaUseCase
) : ViewModel() {
    private val estado = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = estado.asStateFlow()

    fun onCampoCambiado(campos: CamposRegistro) {
        estado.update { actual ->
            val errores = if (actual.errores.hayErrores) validarOferta(campos) else actual.errores
            actual.copy(campos = campos, errores = errores, mensaje = null)
        }
    }

    fun publicar() {
        if (estado.value.guardando || estado.value.guardadoExitoso) return
        val campos = estado.value.campos
        val errores = validarOferta(campos)
        if (errores.hayErrores) {
            estado.update { it.copy(errores = errores) }
            return
        }
        estado.update { it.copy(guardando = true, mensaje = null) }
        viewModelScope.launch {
            try {
                when (val resultado = registrarOferta(campos)) {
                    is ResultadoOperacion.Exito -> estado.update { it.copy(guardadoExitoso = true) }
                    is ResultadoOperacion.Error -> estado.update { it.copy(mensaje = resultado.mensaje) }
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                estado.update { it.copy(mensaje = "No se pudo guardar la oferta. Intenta nuevamente.") }
            } finally {
                estado.update { it.copy(guardando = false) }
            }
        }
    }
}
