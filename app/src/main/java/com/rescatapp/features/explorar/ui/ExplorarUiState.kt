package com.rescatapp.features.explorar.ui

import com.rescatapp.core.model.Comercio
import com.rescatapp.core.model.Oferta
import com.rescatapp.features.explorar.domain.FiltrosOfertas
import com.rescatapp.features.explorar.domain.TipoBusquedaExplorar

data class ExplorarUiState(
    val filtros: FiltrosOfertas = FiltrosOfertas(),
    val ofertas: List<Oferta> = emptyList(),
    val comercios: List<Comercio> = emptyList()
) {
    val ofertasVisibles: List<Oferta>
        get() = if (filtros.tipo == TipoBusquedaExplorar.NEGOCIOS &&
            filtros.comercioSeleccionado == null
        ) {
            emptyList()
        } else {
            ofertas
        }

    val comerciosVisibles: List<Comercio>
        get() = if (filtros.tipo == TipoBusquedaExplorar.PRODUCTOS ||
            filtros.comercioSeleccionado != null
        ) {
            emptyList()
        } else {
            comercios
        }

    val cantidad: Int
        get() = ofertasVisibles.count()

    val cantidadComercios: Int
        get() = comerciosVisibles.count()

    val sinResultados: Boolean
        get() = ofertasVisibles.isEmpty() && comerciosVisibles.isEmpty()
}
