package com.rescatapp.features.explorar.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas

enum class TipoBusquedaExplorar(val etiqueta: String) {
    TODO("Todo"),
    PRODUCTOS("Productos"),
    NEGOCIOS("Negocios")
}

data class FiltrosOfertas(
    val texto: String = "",
    val categoria: Categoria? = null,
    val orden: OrdenOfertas = OrdenOfertas.RECOMENDADAS,
    val tipo: TipoBusquedaExplorar = TipoBusquedaExplorar.TODO,
    val comercioSeleccionado: String? = null
)
