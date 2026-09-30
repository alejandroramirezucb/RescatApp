package com.rescatapp.features.inicio.domain

import com.rescatapp.core.model.Oferta

data class OfertasInicio(
    val disponibles: List<Oferta> = emptyList(),
    val porAgotarse: List<Oferta> = emptyList(),
    val mejoresDelDia: List<Oferta> = emptyList(),
    val postres: List<Oferta> = emptyList()
)
