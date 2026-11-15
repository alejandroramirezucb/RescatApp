package com.rescatapp.features.auth.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.RegistrarUsuarioUseCase
import com.rescatapp.features.auth.domain.ValidarRegistroUsuarioUseCase
import com.rescatapp.features.auth.domain.model.SesionUsuario
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
    private val validarRegistroUsuarioUseCase: ValidarRegistroUsuarioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUsuarioUiState())
    val uiState: StateFlow<RegistroUsuarioUiState> = _uiState.asStateFlow()

    fun onNombreCambiado(nombre: String) {
        _uiState.update { estado ->
            estado.copy(
                datos = estado.datos.copy(nombre = nombre),
                errores = estado.errores.copy(nombre = null),
                errorGeneral = null
            )
        }
    }

    fun onCorreoCambiado(correo: String) {
        _uiState.update { estado ->
            estado.copy(
                datos = estado.datos.copy(correo = correo),
                errores = estado.errores.copy(correo = null),
                errorGeneral = null
            )
        }
    }

    fun onContrasenaCambia(contrasena: String) {
        _uiState.update { estado ->
            estado.copy(
                datos = estado.datos.copy(contrasena = contrasena),
                errores = estado.errores.copy(contrasena = null),
                errorGeneral = null
            )
        }
    }

    fun onConfirmarContrasenaCambia(confirmar: String) {
        _uiState.update { estado ->
            estado.copy(
                datos = estado.datos.copy(confirmarContrasena = confirmar),
                errores = estado.errores.copy(confirmarContrasena = null),
                errorGeneral = null
            )
        }
    }

    fun onRolCambiado(rol: RolUsuario) {
        _uiState.update { estado ->
            estado.copy(
                datos = estado.datos.copy(rol = rol),
                errores = estado.errores.copy(nombreNegocio = null),
                errorGeneral = null
            )
        }
    }

    fun onTelefonoCambiado(telefono: String) {
        _uiState.update { estado ->
            estado.copy(datos = estado.datos.copy(telefono = telefono))
        }
    }

    fun onDireccionCambia(direccion: String) {
        _uiState.update { estado ->
            estado.copy(datos = estado.datos.copy(direccion = direccion))
        }
    }

    fun onNombreNegocioCambiado(nombreNegocio: String) {
        _uiState.update { estado ->
            estado.copy(
                datos = estado.datos.copy(nombreNegocio = nombreNegocio),
                errores = estado.errores.copy(nombreNegocio = null),
                errorGeneral = null
            )
        }
    }

    fun onToggleVisibilidadContrasena() {
        _uiState.update { it.copy(contrasenaVisible = !it.contrasenaVisible) }
    }

    fun onToggleVisibilidadConfirmarContrasena() {
        _uiState.update { it.copy(confirmarContrasenaVisible = !it.confirmarContrasenaVisible) }
    }

    fun registrarUsuario(onExito: (SesionUsuario) -> Unit = {}) {
        if (_uiState.value.cargando || _uiState.value.sesionExitosa != null) return
        val datos = _uiState.value.datos
        val errores = validarRegistroUsuarioUseCase(datos)
        if (errores.hayErrores) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(cargando = true, errorGeneral = null) }
        viewModelScope.launch {
            when (val resultado = registrarUsuarioUseCase(datos)) {
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
