package com.rescatapp.features.inicio.domain

import com.rescatapp.core.model.ImpactoSemanal
import com.rescatapp.core.model.Oferta

data class InicioUiState(
    val ofertasCerca: List<Oferta> = emptyList(),
    val ofertasAgotando: List<Oferta> = emptyList(),
    val impacto: ImpactoSemanal = ImpactoSemanal(0, 0.0, 0.0)
)
