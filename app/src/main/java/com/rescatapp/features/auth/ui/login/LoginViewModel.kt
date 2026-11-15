package com.rescatapp.features.auth.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.auth.domain.IniciarSesionUseCase
import com.rescatapp.features.auth.domain.ValidarLoginUseCase
import com.rescatapp.features.auth.domain.model.SesionUsuario
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
    private val validarLoginUseCase: ValidarLoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onCorreoCambiado(correo: String) {
        _uiState.update { estado ->
            val nuevasCredenciales = estado.credenciales.copy(correo = correo)
            estado.copy(
                credenciales = nuevasCredenciales,
                errores = estado.errores.copy(correo = null),
                errorGeneral = null,
                sesionExitosa = null
            )
        }
    }

    fun onContrasenaCambia(contrasena: String) {
        _uiState.update { estado ->
            val nuevasCredenciales = estado.credenciales.copy(contrasena = contrasena)
            estado.copy(
                credenciales = nuevasCredenciales,
                errores = estado.errores.copy(contrasena = null),
                errorGeneral = null,
                sesionExitosa = null
            )
        }
    }

    fun onToggleVisibilidadContrasena() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun iniciarSesion(onExito: (SesionUsuario) -> Unit = {}) {
        if (_uiState.value.cargando || _uiState.value.sesionExitosa != null) return
        val credenciales = _uiState.value.credenciales
        val errores = validarLoginUseCase(credenciales)
        if (errores.hayErrores) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            when (val resultado = iniciarSesionUseCase(credenciales)) {
                is ResultadoOperacion.Exito -> {
                    val sesion = resultado.valor as SesionUsuario
                    _uiState.update { it.copy(cargando = false, sesionExitosa = sesion) }
                    onExito(sesion)
                }

                is ResultadoOperacion.Error -> {
                    _uiState.update {
                        it.copy(cargando = false, errorGeneral = resultado.mensaje)
                    }
                }
            }
        }
    }

    fun iniciarSesionDemo(esNegocio: Boolean, onExito: (SesionUsuario) -> Unit = {}) {
        if (_uiState.value.cargando || _uiState.value.sesionExitosa != null) return
        _uiState.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            when (val resultado = iniciarSesionUseCase.demo(esNegocio)) {
                is ResultadoOperacion.Exito -> {
                    val sesion = resultado.valor as SesionUsuario
                    _uiState.update { it.copy(cargando = false, sesionExitosa = sesion) }
                    onExito(sesion)
                }

                is ResultadoOperacion.Error -> {
                    _uiState.update {
                        it.copy(cargando = false, errorGeneral = resultado.mensaje)
                    }
                }
            }
        }
    }

    fun consumirSesionExitosa() {
        _uiState.update { it.copy(sesionExitosa = null) }
    }

    fun limpiarError() {
        _uiState.update { it.copy(errorGeneral = null) }
    }
}
