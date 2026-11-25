package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.core.model.agruparComercios
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.explorar.domain.FiltrosOfertas
import com.rescatapp.features.explorar.domain.TipoBusquedaExplorar
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExplorarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repositorioOfertas: RepositorioOfertas
) : ViewModel() {
    private val _filtros = MutableStateFlow(
        FiltrosOfertas(
            categoria = savedStateHandle.categoriaSolicitada(),
            comercioSeleccionado = savedStateHandle.comercioSolicitado(),
            tipo = savedStateHandle.tipoSolicitado()
        )
    )
    val filtros: StateFlow<FiltrosOfertas> = _filtros

    val uiState: StateFlow<ExplorarUiState> =
        _filtros.flatMapLatest { filtrosActuales ->
            repositorioOfertas.filtrar(filtrosActuales.categoria, "")
                .map { ofertasDeCategoria ->
                    val texto = filtrosActuales.texto.trim()
                    val comercioActivo = filtrosActuales.comercioSeleccionado
                    val porDescuento = filtrosActuales.orden == OrdenOfertas.MAYOR_DESCUENTO
                    val ofertas = ofertasDeCategoria.filter {
                        (comercioActivo == null || it.comercio.equals(comercioActivo, true)) &&
                            (it.nombre.contains(texto, true) || it.comercio.contains(texto, true))
                    }
                    val comercios = ofertasDeCategoria.agruparComercios()
                        .filter { it.nombre.contains(texto, true) }
                    ExplorarUiState(
                        filtros = filtrosActuales,
                        ofertas = if (porDescuento) {
                            ofertas.sortedByDescending { it.porcentajeDescuento }
                        } else {
                            ofertas
                        },
                        comercios = if (porDescuento) {
                            comercios.sortedByDescending { it.mejorDescuento }
                        } else {
                            comercios
                        }
                    )
                }
        }.stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = ExplorarUiState(filtros = _filtros.value)
        )

    fun buscar(texto: String) = _filtros.update { it.copy(texto = texto) }

    fun seleccionarCategoria(categoria: Categoria?) =
        _filtros.update { it.copy(categoria = categoria) }

    fun seleccionarOrden(orden: OrdenOfertas) = _filtros.update { it.copy(orden = orden) }

    fun seleccionarTipoBusqueda(tipo: TipoBusquedaExplorar) =
        _filtros.update { it.copy(tipo = tipo, comercioSeleccionado = null) }

    fun seleccionarComercio(comercio: String?) =
        _filtros.update { it.copy(comercioSeleccionado = comercio) }

    fun limpiarFiltros() = _filtros.update { FiltrosOfertas() }

    private fun SavedStateHandle.categoriaSolicitada(): Categoria? {
        val nombreCategoria: String? = get(ArgumentosRuta.CATEGORIA)
        return Categoria.entries.find { it.name == nombreCategoria }
    }

    private fun SavedStateHandle.comercioSolicitado(): String? =
        get<String>(ArgumentosRuta.COMERCIO)?.takeIf { it.isNotBlank() }

    private fun SavedStateHandle.tipoSolicitado(): TipoBusquedaExplorar {
        val nombreTipo: String? = get(ArgumentosRuta.TIPO)
        return TipoBusquedaExplorar.entries.find { it.name == nombreTipo }
            ?: TipoBusquedaExplorar.TODO
    }
}
