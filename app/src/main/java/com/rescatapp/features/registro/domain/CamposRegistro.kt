package com.rescatapp.features.registro.domain

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.util.hoyEnBolivia

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
    val horaRetiroHasta: String = "",
    val imagenUri: String? = null,
    val fechaRetiro: String = hoyEnBolivia().toString()
)
