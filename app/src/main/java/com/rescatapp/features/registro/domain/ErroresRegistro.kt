package com.rescatapp.features.registro.domain

data class ErroresRegistro(
    val nombre: String? = null,
    val comercio: String? = null,
    val categoria: String? = null,
    val descripcion: String? = null,
    val pesoKg: String? = null,
    val precioNormal: String? = null,
    val precioRescate: String? = null,
    val cantidadDisponible: String? = null,
    val horaRetiroDesde: String? = null,
    val horaRetiroHasta: String? = null
) {
    val hayErrores: Boolean
        get() = listOfNotNull(
            nombre,
            comercio,
            categoria,
            descripcion,
            pesoKg,
            precioNormal,
            precioRescate,
            cantidadDisponible,
            horaRetiroDesde,
            horaRetiroHasta
        ).isNotEmpty()
}
