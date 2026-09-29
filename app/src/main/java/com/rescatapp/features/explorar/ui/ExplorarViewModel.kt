package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.data.repository.RepositorioOfertas
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.ExplorarUiState
import com.rescatapp.features.explorar.domain.FiltrarOfertasUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ExplorarViewModel @Inject constructor(
    repositorio: RepositorioOfertas,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val filtro = FiltrarOfertasUseCase()
    private val nombreCategoria: String? = savedStateHandle["categoria"]
    private val categoriaInicial = Categoria.entries.find { it.name == nombreCategoria }
    private val texto = MutableStateFlow("")
    private val categoria = MutableStateFlow(categoriaInicial)
    private val orden = MutableStateFlow(OrdenOfertas.RECOMENDADAS)

    val uiState = combine(repositorio.ofertas, texto, categoria, orden) {
            ofertas,
            busqueda,
            seleccion,
            ordenActual
        ->
        ExplorarUiState(
            texto = busqueda,
            categoria = seleccion,
            orden = ordenActual,
            ofertas = filtro(ofertas, busqueda, seleccion, ordenActual)
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ExplorarUiState(
            categoria = categoriaInicial,
            ofertas = filtro(
                repositorio.ofertas.value,
                "",
                categoriaInicial,
                OrdenOfertas.RECOMENDADAS
            )
        )
    )

    fun buscar(valor: String) {
        texto.value = valor
    }

    fun seleccionarCategoria(valor: Categoria?) {
        categoria.value = valor
    }

    fun seleccionarOrden(valor: OrdenOfertas) {
        orden.value = valor
    }
}
