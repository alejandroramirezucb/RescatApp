package com.rescatapp.features.inicio.domain

import com.rescatapp.core.model.Disponibilidad
import com.rescatapp.core.model.Oferta
import javax.inject.Inject

class SeleccionarOfertasInicioUseCase @Inject constructor() {
    operator fun invoke(ofertas: List<Oferta>): OfertasInicio {
        val disponibles = ofertas.filterNot { it.estaAgotada }.sortedByDescending { it.id }
        return OfertasInicio(
            disponibles = disponibles,
            porAgotarse = disponibles.filter { it.disponibilidad == Disponibilidad.BAJA }
        )
    }
}
