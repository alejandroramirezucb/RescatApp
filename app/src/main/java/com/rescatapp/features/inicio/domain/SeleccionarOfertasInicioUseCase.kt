package com.rescatapp.features.inicio.domain

import com.rescatapp.core.model.Oferta
import javax.inject.Inject

data class OfertasInicio(
    val ofertasDisponibles: List<Oferta> = emptyList(),
    val seEstanAgotando: List<Oferta> = emptyList()
)

class SeleccionarOfertasInicioUseCase @Inject constructor() {
    operator fun invoke(ofertas: List<Oferta>): OfertasInicio {
        val disponibles = ofertas
            .filterNot { it.estaAgotada }
            .sortedByDescending { it.id }

        val agotando = ofertas
            .filter { !it.estaAgotada && it.cantidadDisponible in 1..2 }
            .sortedByDescending { it.id }

        return OfertasInicio(
            ofertasDisponibles = disponibles,
            seEstanAgotando = agotando
        )
    }
}
