package com.rescatapp.features.registro.ui

import androidx.lifecycle.ViewModel
import com.rescatapp.core.model.ResultadoOperacion
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.ErroresRegistro
import com.rescatapp.features.registro.domain.RegistrarOfertaUseCase
import com.rescatapp.features.registro.domain.RegistroUiState
import com.rescatapp.features.registro.domain.ValidarOfertaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val validar: ValidarOfertaUseCase,
    private val registrar: RegistrarOfertaUseCase
) : ViewModel() {
    private val estado = MutableStateFlow(RegistroUiState())
    val uiState = estado.asStateFlow()

    fun onCampoCambiado(campos: CamposRegistro) {
        val errores = if (estado.value.errores.hayErrores) validar(campos) else ErroresRegistro()
        estado.value = estado.value.copy(campos = campos, errores = errores, mensaje = null)
    }

    fun publicar() {
        val campos = estado.value.campos
        val errores = validar(campos)
        if (errores.hayErrores) {
            estado.value = estado.value.copy(errores = errores, guardadoExitoso = false)
            return
        }

        val resultado = registrar(campos)
        estado.value = estado.value.copy(
            errores = errores,
            guardadoExitoso = resultado is ResultadoOperacion.Exito,
            mensaje = if (resultado is ResultadoOperacion.Error) resultado.mensaje else null
        )
    }
}
