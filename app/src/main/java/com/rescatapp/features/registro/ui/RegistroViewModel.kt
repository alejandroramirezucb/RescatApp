package com.rescatapp.features.registro.ui

import androidx.lifecycle.ViewModel
import com.rescatapp.core.model.Categoria
import com.rescatapp.features.registro.domain.CamposRegistro
import com.rescatapp.features.registro.domain.ErroresRegistro
import com.rescatapp.features.registro.domain.RegistrarOfertaUseCase
import com.rescatapp.features.registro.domain.RegistroUiState
import com.rescatapp.features.registro.domain.ValidarOfertaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val validarOfertaUseCase: ValidarOfertaUseCase,
    private val registrarOfertaUseCase: RegistrarOfertaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    fun onCampoCambiado(nuevosCampos: CamposRegistro) {
        _uiState.update { actual ->
            val anteriores = actual.campos
            val erroresActuales = actual.errores

            val nuevosErrores = erroresActuales.copy(
                nombre = if (nuevosCampos.nombre != anteriores.nombre) {
                    null
                } else {
                    erroresActuales.nombre
                },
                comercio = if (nuevosCampos.comercio != anteriores.comercio) {
                    null
                } else {
                    erroresActuales.comercio
                },
                categoria = if (nuevosCampos.categoria != anteriores.categoria) {
                    null
                } else {
                    erroresActuales.categoria
                },
                descripcion = if (nuevosCampos.descripcion != anteriores.descripcion) {
                    null
                } else {
                    erroresActuales.descripcion
                },
                pesoKg = if (nuevosCampos.pesoKg != anteriores.pesoKg) {
                    null
                } else {
                    erroresActuales.pesoKg
                },
                precioNormal = if (nuevosCampos.precioNormal != anteriores.precioNormal) {
                    null
                } else {
                    erroresActuales.precioNormal
                },
                precioRescate = if (nuevosCampos.precioRescate != anteriores.precioRescate) {
                    null
                } else {
                    erroresActuales.precioRescate
                },
                cantidadDisponible = if (
                    nuevosCampos.cantidadDisponible != anteriores.cantidadDisponible
                ) {
                    null
                } else {
                    erroresActuales.cantidadDisponible
                },
                horaRetiroDesde = if (
                    nuevosCampos.horaRetiroDesde != anteriores.horaRetiroDesde
                ) {
                    null
                } else {
                    erroresActuales.horaRetiroDesde
                },
                horaRetiroHasta = if (
                    nuevosCampos.horaRetiroHasta != anteriores.horaRetiroHasta
                ) {
                    null
                } else {
                    erroresActuales.horaRetiroHasta
                }
            )

            actual.copy(campos = nuevosCampos, errores = nuevosErrores)
        }
    }

    fun onCampoCambiado(
        nombre: String? = null,
        comercio: String? = null,
        categoria: Categoria? = null,
        descripcion: String? = null,
        pesoKg: String? = null,
        precioNormal: String? = null,
        precioRescate: String? = null,
        cantidadDisponible: String? = null,
        horaRetiroDesde: String? = null,
        horaRetiroHasta: String? = null,
        horaDesde: String? = null,
        horaHasta: String? = null
    ) {
        val actuales = _uiState.value.campos
        val desde = horaRetiroDesde ?: horaDesde ?: actuales.horaRetiroDesde
        val hasta = horaRetiroHasta ?: horaHasta ?: actuales.horaHasta
        val nuevos = actuales.copy(
            nombre = nombre ?: actuales.nombre,
            comercio = comercio ?: actuales.comercio,
            categoria = categoria ?: actuales.categoria,
            descripcion = descripcion ?: actuales.descripcion,
            pesoKg = pesoKg ?: actuales.pesoKg,
            precioNormal = precioNormal ?: actuales.precioNormal,
            precioRescate = precioRescate ?: actuales.precioRescate,
            cantidadDisponible = cantidadDisponible ?: actuales.cantidadDisponible,
            horaRetiroDesde = desde,
            horaRetiroHasta = hasta
        )
        onCampoCambiado(nuevos)
    }

    fun onNombreCambiado(valor: String) = onCampoCambiado(nombre = valor)
    fun onComercioCambiado(valor: String) = onCampoCambiado(comercio = valor)
    fun onCategoriaCambiada(valor: Categoria?) = onCampoCambiado(categoria = valor)
    fun onDescripcionCambiado(valor: String) = onCampoCambiado(descripcion = valor)
    fun onPesoKgCambiado(valor: String) = onCampoCambiado(pesoKg = valor)
    fun onPrecioNormalCambiado(valor: String) = onCampoCambiado(precioNormal = valor)
    fun onPrecioRescateCambiado(valor: String) = onCampoCambiado(precioRescate = valor)
    fun onCantidadDisponibleCambiado(valor: String) = onCampoCambiado(cantidadDisponible = valor)
    fun onHoraRetiroDesdeCambiado(valor: String) = onCampoCambiado(horaRetiroDesde = valor)
    fun onHoraRetiroHastaCambiado(valor: String) = onCampoCambiado(horaRetiroHasta = valor)

    fun publicar() {
        val camposActuales = _uiState.value.campos
        val errores = validarOfertaUseCase(camposActuales)

        if (errores.hayErrores) {
            _uiState.update { actual ->
                actual.copy(
                    errores = errores,
                    guardadoExitoso = false
                )
            }
        } else {
            val resultado = registrarOfertaUseCase(camposActuales)
            if (resultado != null) {
                _uiState.update { actual ->
                    actual.copy(
                        errores = ErroresRegistro(),
                        guardadoExitoso = true
                    )
                }
            } else {
                _uiState.update { actual ->
                    actual.copy(guardadoExitoso = false)
                }
            }
        }
    }

    fun reiniciarGuardado() {
        _uiState.update { it.copy(guardadoExitoso = false) }
    }
}
