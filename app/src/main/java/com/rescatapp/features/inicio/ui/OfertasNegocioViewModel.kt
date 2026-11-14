package com.rescatapp.features.inicio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.remote.FuenteOfertasRemotas
import com.rescatapp.core.model.EstadoRemoto
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo
import com.rescatapp.features.inicio.domain.EditarOfertaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OfertasNegocioViewModel @Inject constructor(
    private val repositorioOfertas: RepositorioOfertas,
    private val repositorioRemoto: FuenteOfertasRemotas,
    private val editarOferta: EditarOfertaUseCase
) : ViewModel() {
    private val estado = MutableStateFlow(OfertasNegocioUiState())
    val uiState = estado.asStateFlow()
    private var ofertasRemotas = emptyList<Oferta>()

    init {
        viewModelScope.launch {
            repositorioOfertas.ofertas.collect { ofertas ->
                val propias = ofertas.filter { it.comercio == UsuarioDemo.NEGOCIO_DEMO }
                val (finalizadas, activas) = propias.partition(Oferta::estaAgotada)
                estado.update { it.copy(activas = activas, finalizadas = finalizadas) }
            }
        }
    }

    fun seleccionarPestana(pestana: PestanaOfertas) {
        val anterior = estado.value.pestana
        estado.update { it.copy(pestana = pestana) }
        if (pestana == PestanaOfertas.EN_LINEA && anterior != pestana) cargarOfertasRemotas()
    }

    fun actualizarOferta(oferta: Oferta, onGuardada: () -> Unit = {}) {
        if (estado.value.ocupado) return
        estado.update { it.copy(guardandoLocal = true, errorLocal = null) }
        viewModelScope.launch {
            try {
                editarOferta(oferta)
                onGuardada()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                estado.update {
                    it.copy(errorLocal = "No se pudieron guardar los cambios. Intenta nuevamente.")
                }
            } finally {
                estado.update { it.copy(guardandoLocal = false) }
            }
        }
    }

    fun eliminarOfertaLocal(oferta: Oferta, onEliminada: () -> Unit = {}) {
        if (estado.value.ocupado) return
        estado.update { it.copy(eliminandoLocal = true, errorLocal = null, mensaje = null) }
        viewModelScope.launch {
            try {
                repositorioOfertas.eliminar(oferta)
                onEliminada()
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (_: Exception) {
                estado.update {
                    it.copy(errorLocal = "No se pudo eliminar la oferta. Intenta nuevamente.")
                }
            } finally {
                estado.update { it.copy(eliminandoLocal = false) }
            }
        }
    }

    fun cargarOfertasRemotas() {
        if (estado.value.ocupado) return
        estado.update { it.copy(estadoRemoto = EstadoRemoto.Cargando) }
        viewModelScope.launch {
            try {
                ofertasRemotas = repositorioRemoto.obtenerOfertasRemotas()
                    .filter { it.comercio == UsuarioDemo.NEGOCIO_DEMO }
                estado.update { it.copy(estadoRemoto = EstadoRemoto.Exito(ofertasRemotas)) }
            } catch (cancelacion: CancellationException) {
                estado.update { it.copy(estadoRemoto = EstadoRemoto.Exito(ofertasRemotas)) }
                throw cancelacion
            } catch (error: Exception) {
                estado.update { it.copy(estadoRemoto = EstadoRemoto.Error(mensajeDeError(error))) }
            }
        }
    }

    fun publicarEnLinea(oferta: Oferta) =
        ejecutarOperacion(OperacionOferta(TipoOperacionOferta.PUBLICAR, oferta))

    fun editarOfertaRemota(oferta: Oferta) =
        ejecutarOperacion(OperacionOferta(TipoOperacionOferta.EDITAR, oferta))

    fun eliminarOfertaRemota(oferta: Oferta) =
        ejecutarOperacion(OperacionOferta(TipoOperacionOferta.ELIMINAR, oferta))

    fun reintentarOperacion() {
        estado.value.operacionFallida?.let(::ejecutarOperacion)
    }

    fun limpiarMensaje() = estado.update { it.copy(mensaje = null, errorLocal = null) }

    private fun ejecutarOperacion(operacion: OperacionOferta) {
        if (estado.value.ocupado) return
        estado.update {
            it.copy(
                operacionEnCurso = operacion,
                operacionFallida = null,
                errorOperacion = null,
                mensaje = null
            )
        }
        viewModelScope.launch {
            var consultarDespuesDePublicar = false
            try {
                ofertasRemotas = repositorioRemoto.aplicarOperacion(operacion, ofertasRemotas)
                consultarDespuesDePublicar = operacion.tipo == TipoOperacionOferta.PUBLICAR
                estado.update {
                    it.copy(
                        estadoRemoto = EstadoRemoto.Exito(ofertasRemotas),
                        pestana = PestanaOfertas.EN_LINEA,
                        mensaje = mensajeDeExito(operacion.tipo)
                    )
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                estado.update {
                    it.copy(errorOperacion = mensajeDeError(error), operacionFallida = operacion)
                }
            } finally {
                estado.update { it.copy(operacionEnCurso = null) }
            }
            if (consultarDespuesDePublicar) cargarOfertasRemotas()
        }
    }
}
