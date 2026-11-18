package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.remote.mensajeDeError
import com.rescatapp.core.model.Oferta
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OfertasNegocioViewModel @Inject constructor(
    private val repositorioOfertas: RepositorioOfertas,
    repositorioAutenticacion: RepositorioAutenticacion
) : ViewModel() {
    private val estado = MutableStateFlow(OfertasNegocioUiState())
    val uiState = estado.asStateFlow()

    init {
        viewModelScope.launch {
            repositorioAutenticacion.sesionActual.collect { sesion ->
                estado.update { it.copy(nombreNegocio = sesion?.nombreNegocio.orEmpty()) }
            }
        }
        viewModelScope.launch {
            repositorioOfertas.propias.collect { propias ->
                val (finalizadas, activas) = propias.partition {
                    it.estaAgotada || it.estadoPublicacion != "ACTIVA"
                }
                estado.update { it.copy(activas = activas, finalizadas = finalizadas) }
            }
        }
    }

    fun seleccionarPestana(pestana: PestanaOfertas) = estado.update { it.copy(pestana = pestana) }

    fun actualizarOferta(oferta: Oferta, onGuardada: () -> Unit = {}) = operar {
        repositorioOfertas.actualizar(oferta)
        onGuardada()
    }

    fun eliminarOferta(oferta: Oferta, onEliminada: () -> Unit = {}) = operar {
        repositorioOfertas.eliminar(oferta)
        onEliminada()
    }

    fun cambiarEstado(oferta: Oferta) = operar { repositorioOfertas.cambiarEstado(oferta) }

    fun limpiarError() = estado.update { it.copy(error = null) }

    private fun operar(accion: suspend () -> Unit) {
        if (estado.value.ocupado) return
        estado.update { it.copy(ocupado = true, error = null) }
        viewModelScope.launch {
            try {
                accion()
            } catch (error: Exception) {
                estado.update { it.copy(error = mensajeDeError(error)) }
            } finally {
                estado.update { it.copy(ocupado = false) }
            }
        }
    }
}
