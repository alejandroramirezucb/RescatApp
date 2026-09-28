package com.rescatapp.features.registro.domain

import com.rescatapp.core.model.Categoria

data class CamposRegistro(
    val nombre: String = "",
    val comercio: String = "",
    val categoria: Categoria? = null,
    val descripcion: String = "",
    val pesoKg: String = "",
    val precioNormal: String = "",
    val precioRescate: String = "",
    val cantidadDisponible: String = "",
    val horaDesde: String = "",
    val horaHasta: String = ""
) {
    val peso: String get() = pesoKg
    val cantidad: String get() = cantidadDisponible
    val horaRetiroDesde: String get() = horaDesde
    val horaRetiroHasta: String get() = horaHasta
}
