package com.rescatapp.features.inicio.domain

import com.rescatapp.core.domain.Impacto
import com.rescatapp.core.model.Oferta

data class InicioUiState(
    val usuario: String = "Invitado",
    val impacto: Impacto = Impacto(),
    val ofertasDisponibles: List<Oferta> = emptyList(),
    val seEstanAgotando: List<Oferta> = emptyList()
) {
    val ofertasCerca: List<Oferta> get() = ofertasDisponibles
    val ofertasAgotando: List<Oferta> get() = seEstanAgotando

    constructor(
        ofertasCerca: List<Oferta>,
        ofertasAgotando: List<Oferta>,
        impacto: Impacto
    ) : this(
        usuario = "Invitado",
        impacto = impacto,
        ofertasDisponibles = ofertasCerca,
        seEstanAgotando = ofertasAgotando
    )
}
