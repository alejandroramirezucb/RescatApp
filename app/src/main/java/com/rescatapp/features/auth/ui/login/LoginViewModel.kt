package com.rescatapp.features.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.auth.domain.IniciarSesionUseCase
import com.rescatapp.features.auth.domain.ValidarLoginUseCase
import com.rescatapp.features.auth.domain.model.CredencialesLogin
import com.rescatapp.features.auth.domain.model.ErroresLogin
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
    private val validarLogin: ValidarLoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun actualizarCredenciales(credenciales: CredencialesLogin) {
        _uiState.update {
            it.copy(credenciales = credenciales, errores = ErroresLogin(), errorGeneral = null)
        }
    }

    fun iniciarSesion() {
        val credenciales = _uiState.value.credenciales
        val errores = validarLogin(credenciales)
        if (errores.hayErrores) {
            _uiState.update { it.copy(errores = errores) }
            return
        }
        if (_uiState.value.cargando || _uiState.value.sesionIniciada) return
        _uiState.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            val resultado = iniciarSesionUseCase(credenciales)
            _uiState.update {
                it.copy(
                    cargando = false,
                    sesionIniciada = resultado is ResultadoOperacion.Exito,
                    errorGeneral = (resultado as? ResultadoOperacion.Error)?.mensaje
                )
            }
        }
    }
}
