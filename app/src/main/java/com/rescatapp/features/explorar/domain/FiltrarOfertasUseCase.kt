package com.rescatapp.features.explorar.domain

import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.OrdenOfertas
import javax.inject.Inject

class FiltrarOfertasUseCase @Inject constructor() {
    operator fun invoke(ofertas: List<Oferta>, filtros: FiltrosOfertas): List<Oferta> {
        val ofertasFiltradas = ofertas.filter { oferta ->
            oferta.perteneceA(filtros) && oferta.coincideCon(filtros.texto.trim())
        }
        val masRecientesPrimero = ofertasFiltradas.sortedByDescending { it.id }
        return when (filtros.orden) {
            OrdenOfertas.RECOMENDADAS -> masRecientesPrimero

            OrdenOfertas.MAYOR_DESCUENTO ->
                masRecientesPrimero.sortedByDescending { it.porcentajeDescuento }
        }
    }

    private fun Oferta.perteneceA(filtros: FiltrosOfertas): Boolean =
        filtros.categoria == null || categoria == filtros.categoria

    private fun Oferta.coincideCon(busqueda: String): Boolean = busqueda.isEmpty() ||
        nombre.contains(busqueda, ignoreCase = true) ||
        comercio.contains(busqueda, ignoreCase = true)
}
