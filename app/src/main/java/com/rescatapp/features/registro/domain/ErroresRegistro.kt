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
    val horaDesde: String? = null,
    val horaHasta: String? = null
) {
    val peso: String? get() = pesoKg
    val cantidad: String? get() = cantidadDisponible
    val horaRetiroDesde: String? get() = horaDesde
    val horaRetiroHasta: String? get() = horaHasta

    val hayErrores: Boolean
        get() = nombre != null ||
            comercio != null ||
            categoria != null ||
            descripcion != null ||
            pesoKg != null ||
            precioNormal != null ||
            precioRescate != null ||
            cantidadDisponible != null ||
            horaDesde != null ||
            horaHasta != null
}
