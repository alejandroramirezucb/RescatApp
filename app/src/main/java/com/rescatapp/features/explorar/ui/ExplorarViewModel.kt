package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.explorar.domain.FiltrosOfertas
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
    private val filtros = MutableStateFlow(
        FiltrosOfertas(categoria = savedStateHandle.categoriaSolicitada())
    )

    val uiState: StateFlow<ExplorarUiState> =
        filtros.flatMapLatest { filtrosActuales ->
            repositorioOfertas.filtrar(filtrosActuales.categoria, filtrosActuales.texto)
                .map { ofertasFiltradas ->
                    val ofertasOrdenadas = when (filtrosActuales.orden) {
                        OrdenOfertas.RECOMENDADAS -> ofertasFiltradas

                        OrdenOfertas.MAYOR_DESCUENTO ->
                            ofertasFiltradas.sortedByDescending { it.porcentajeDescuento }
                    }
                    ExplorarUiState(
                        filtros = filtrosActuales,
                        ofertas = ofertasOrdenadas
                    )
                }
        }.stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = ExplorarUiState(filtros = filtros.value)
        )

    fun buscar(texto: String) = filtros.update { it.copy(texto = texto) }

    fun seleccionarCategoria(categoria: Categoria?) =
        filtros.update { it.copy(categoria = categoria) }

    fun seleccionarOrden(orden: OrdenOfertas) = filtros.update { it.copy(orden = orden) }

    private fun SavedStateHandle.categoriaSolicitada(): Categoria? {
        val nombreCategoria: String? = get(ArgumentosRuta.CATEGORIA)
        return Categoria.entries.find { it.name == nombreCategoria }
    }
}
