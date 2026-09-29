package com.rescatapp.features.explorar.ui

import com.rescatapp.core.model.Oferta
import com.rescatapp.features.explorar.domain.FiltrosOfertas

data class ExplorarUiState(
    val filtros: FiltrosOfertas = FiltrosOfertas(),
    val ofertas: List<Oferta> = emptyList()
) {
    val cantidad: Int
        get() = ofertas.count()
}
