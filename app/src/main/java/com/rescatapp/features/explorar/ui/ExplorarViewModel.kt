package com.rescatapp.features.explorar.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rescatapp.core.domain.repository.OfertasRepository
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas
import com.rescatapp.features.explorar.domain.ExplorarUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExplorarViewModel @Inject constructor(
    private val ofertasRepository: OfertasRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Si venimos de otra pantalla con una categoría preseleccionada
    private val categoriaInicialString: String? = savedStateHandle.get<String>("categoria_id")
    private val categoriaInicial: Categoria? = categoriaInicialString?.let {
        runCatching { Categoria.valueOf(it) }.getOrNull()
    }

    private val _textoBusqueda = MutableStateFlow("")
    private val _categoriaFiltro = MutableStateFlow(categoriaInicial)
    private val _orden = MutableStateFlow(OrdenOfertas.RECOMENDADAS)

    val uiState: StateFlow<ExplorarUiState> = combine(
        ofertasRepository.getOfertas(),
        _textoBusqueda,
        _categoriaFiltro,
        _orden
    ) { ofertas, texto, categoria, orden ->
        
        var ofertasFiltradas = ofertas

        // Filtro por categoría
        if (categoria != null) {
            ofertasFiltradas = ofertasFiltradas.filter { it.categoria == categoria }
        }

        // Filtro por texto de búsqueda (nombre o comercio)
        if (texto.isNotBlank()) {
            ofertasFiltradas = ofertasFiltradas.filter {
                it.nombre.contains(texto, ignoreCase = true) || 
                it.comercio.contains(texto, ignoreCase = true)
            }
        }

        // Ordenamiento
        ofertasFiltradas = when (orden) {
            OrdenOfertas.MAYOR_DESCUENTO -> ofertasFiltradas.sortedByDescending { it.porcentajeDescuento }
            OrdenOfertas.RECOMENDADAS -> ofertasFiltradas // Podría ser otro criterio, por ahora lo dejamos tal cual o por disponibilidad
        }

        ExplorarUiState(
            textoBusqueda = texto,
            categoriaFiltro = categoria,
            orden = orden,
            ofertas = ofertasFiltradas,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExplorarUiState(isLoading = true, categoriaFiltro = categoriaInicial)
    )

    fun onBuscarTexto(texto: String) {
        _textoBusqueda.value = texto
    }

    fun onSeleccionarCategoria(categoria: Categoria?) {
        _categoriaFiltro.value = categoria
    }

    fun onSeleccionarOrden(orden: OrdenOfertas) {
        _orden.value = orden
    }
}
