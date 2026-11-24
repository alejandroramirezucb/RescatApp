package com.rescatapp.core.model

data class Comercio(
    val nombre: String,
    val categorias: List<Categoria>,
    val cantidadOfertas: Int,
    val imagenUri: String? = null,
    val mejorDescuento: Int = 0
)

fun List<Oferta>.agruparComercios(): List<Comercio> = filter { it.comercio.isNotBlank() }
    .groupBy { it.comercio }
    .map { (nombre, packs) ->
        Comercio(
            nombre = nombre,
            categorias = packs.map { it.categoria }.distinct(),
            cantidadOfertas = packs.size,
            imagenUri = packs.firstNotNullOfOrNull { it.imagenUri },
            mejorDescuento = packs.maxOf { it.porcentajeDescuento }
        )
    }
