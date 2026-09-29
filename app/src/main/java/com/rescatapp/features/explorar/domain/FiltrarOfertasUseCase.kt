package com.rescatapp.features.explorar.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.OrdenOfertas

class FiltrarOfertasUseCase {
    operator fun invoke(
        ofertas: List<Oferta>,
        texto: String,
        categoria: Categoria?,
        orden: OrdenOfertas
    ): List<Oferta> {
        val busqueda = texto.trim()
        val filtradas = ofertas.filter { oferta ->
            (categoria == null || oferta.categoria == categoria) &&
                (
                    busqueda.isEmpty() ||
                        oferta.nombre.contains(busqueda, ignoreCase = true) ||
                        oferta.comercio.contains(busqueda, ignoreCase = true)
                    )
        }

        return when (orden) {
            OrdenOfertas.RECOMENDADAS -> filtradas.sortedByDescending { it.id }

            OrdenOfertas.MAYOR_DESCUENTO -> filtradas.sortedWith(
                compareByDescending(Oferta::porcentajeDescuento).thenByDescending(Oferta::id)
            )
        }
    }
}
