package com.rescatapp.features.auth.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.auth.domain.RegistrarUsuarioUseCase
import com.rescatapp.features.auth.domain.ValidarRegistroUsuarioUseCase
import com.rescatapp.features.auth.domain.model.DatosRegistro
import com.rescatapp.features.auth.domain.model.ErroresRegistro
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegistroUsuarioViewModel @Inject constructor(
    private val registrarUsuarioUseCase: RegistrarUsuarioUseCase,
    private val validarRegistro: ValidarRegistroUsuarioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUsuarioUiState())
    val uiState: StateFlow<RegistroUsuarioUiState> = _uiState.asStateFlow()

    fun actualizarDatos(datos: DatosRegistro) {
        _uiState.update { it.copy(datos = datos, errores = ErroresRegistro(), errorGeneral = null) }
    }

    fun registrarUsuario() {
        val estado = _uiState.value
        if (estado.cargando || estado.sesionIniciada) return
        val errores = validarRegistro(estado.datos)
        if (errores.hayErrores) {
            _uiState.update { it.copy(errores = errores) }
            return
        }
        _uiState.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            val resultado = registrarUsuarioUseCase(estado.datos)
            _uiState.update {
                it.copy(
                    cargando = false,
                    sesionIniciada =
                        resultado is ResultadoOperacion.Exito &&
                            resultado.valor !is com.rescatapp.core.data.supabase.RegistroPendiente,
                    confirmacionPendiente =
                        resultado is ResultadoOperacion.Exito &&
                            resultado.valor is com.rescatapp.core.data.supabase.RegistroPendiente,
                    errorGeneral = (resultado as? ResultadoOperacion.Error)?.mensaje
                )
            }
        }
    }
}
