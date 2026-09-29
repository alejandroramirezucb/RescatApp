package com.rescatapp.features.explorar.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.OrdenOfertas

data class ExplorarUiState(
    val texto: String = "",
    val categoria: Categoria? = null,
    val orden: OrdenOfertas = OrdenOfertas.RECOMENDADAS,
    val ofertas: List<Oferta> = emptyList()
) {
    val cantidad: Int
        get() = ofertas.count()
}
