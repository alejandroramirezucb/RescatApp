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
    val horaRetiroDesde: String = "",
    val horaRetiroHasta: String = ""
) {
    val horaDesde: String
        get() = horaRetiroDesde

    val horaHasta: String
        get() = horaRetiroHasta
}
