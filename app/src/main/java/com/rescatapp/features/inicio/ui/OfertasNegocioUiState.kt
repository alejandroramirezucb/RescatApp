package com.rescatapp.features.inicio.ui

import com.rescatapp.core.model.Oferta

enum class PestanaOfertas(val titulo: String) {
    ACTIVAS("Activas"),
    FINALIZADAS("Finalizadas")
}

data class OfertasNegocioUiState(
    val nombreNegocio: String = "",
    val pestana: PestanaOfertas = PestanaOfertas.ACTIVAS,
    val activas: List<Oferta> = emptyList(),
    val finalizadas: List<Oferta> = emptyList(),
    val ocupado: Boolean = false,
    val error: String? = null
) {
    val ofertasVisibles: List<Oferta>
        get() = if (pestana == PestanaOfertas.ACTIVAS) activas else finalizadas
}
