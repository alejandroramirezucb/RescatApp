package com.rescatapp.features.explorar.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.OrdenOfertas

data class FiltrosOfertas(
    val texto: String = "",
    val categoria: Categoria? = null,
    val orden: OrdenOfertas = OrdenOfertas.RECOMENDADAS
)
