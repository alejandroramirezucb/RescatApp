package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.core.util.suscripcionPantalla
import com.rescatapp.features.explorar.domain.FiltrarOfertasUseCase
import com.rescatapp.features.explorar.domain.FiltrosOfertas
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class ExplorarViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    repositorioOfertas: RepositorioOfertas,
    private val filtrarOfertas: FiltrarOfertasUseCase
) : ViewModel() {
    private val filtros = MutableStateFlow(
        FiltrosOfertas(categoria = savedStateHandle.categoriaSolicitada())
    )

    val uiState: StateFlow<ExplorarUiState> =
        combine(repositorioOfertas.ofertas, filtros, ::crearEstado).stateIn(
            scope = viewModelScope,
            started = suscripcionPantalla,
            initialValue = crearEstado(repositorioOfertas.ofertas.value, filtros.value)
        )

    fun buscar(texto: String) = filtros.update { it.copy(texto = texto) }

    fun seleccionarCategoria(categoria: Categoria?) =
        filtros.update { it.copy(categoria = categoria) }

    fun seleccionarOrden(orden: OrdenOfertas) = filtros.update { it.copy(orden = orden) }

    private fun crearEstado(ofertas: List<Oferta>, filtrosActuales: FiltrosOfertas) =
        ExplorarUiState(
            filtros = filtrosActuales,
            ofertas = filtrarOfertas(ofertas, filtrosActuales)
        )

    private fun SavedStateHandle.categoriaSolicitada(): Categoria? {
        val nombreCategoria: String? = get(ArgumentosRuta.CATEGORIA)
        return Categoria.entries.find { it.name == nombreCategoria }
    }
}
