package com.rescatapp.features.explorar.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.OrdenOfertas

data class ExplorarUiState(
    val textoBusqueda: String = "",
    val categoriaFiltro: Categoria? = null,
    val orden: OrdenOfertas = OrdenOfertas.RECOMENDADAS,
    val ofertas: List<Oferta> = emptyList(),
    val isLoading: Boolean = true
) {
    val cantidad: Int get() = ofertas.size
}
