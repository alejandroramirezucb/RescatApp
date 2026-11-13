package com.rescatapp.core.data.remote

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta

data class OfertaDto(
    val id: String? = null,
    val nombre: String,
    val comercio: String,
    val categoria: String,
    val descripcion: String,
    val pesoKg: Double,
    val precioNormal: Double,
    val precioRescate: Double,
    val cantidadDisponible: Int,
    val horaRetiroDesde: String,
    val horaRetiroHasta: String
)

fun OfertaDto.aOferta(): Oferta = Oferta(
    id = requireNotNull(id).toInt(),
    nombre = nombre,
    comercio = comercio,
    categoria = Categoria.valueOf(categoria),
    descripcion = descripcion,
    pesoKg = pesoKg,
    precioNormal = precioNormal,
    precioRescate = precioRescate,
    cantidadDisponible = cantidadDisponible,
    horaRetiroDesde = horaRetiroDesde,
    horaRetiroHasta = horaRetiroHasta
)

fun Oferta.aOfertaDto(): OfertaDto = OfertaDto(
    nombre = nombre,
    comercio = comercio,
    categoria = categoria.name,
    descripcion = descripcion,
    pesoKg = pesoKg,
    precioNormal = precioNormal,
    precioRescate = precioRescate,
    cantidadDisponible = cantidadDisponible,
    horaRetiroDesde = horaRetiroDesde,
    horaRetiroHasta = horaRetiroHasta
)
