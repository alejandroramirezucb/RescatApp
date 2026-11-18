package com.rescatapp.core.data.remote

import com.google.gson.annotations.SerializedName
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import java.time.LocalDate

data class OfertaDto(
    val id: Int,
    val nombre: String,
    val categoria: String,
    val descripcion: String,
    @SerializedName("peso_kg") val pesoKg: Double,
    @SerializedName("precio_normal") val precioNormal: Double,
    @SerializedName("precio_rescate") val precioRescate: Double,
    @SerializedName("cantidad_disponible") val cantidadDisponible: Int,
    @SerializedName("fecha_retiro") val fechaRetiro: String,
    @SerializedName("hora_retiro_desde") val horaRetiroDesde: String,
    @SerializedName("hora_retiro_hasta") val horaRetiroHasta: String,
    @SerializedName("imagen_url") val imagenUrl: String?,
    val estado: String,
    val negocios: NegocioOfertaDto?
) {
    fun toDomain(): Oferta = Oferta(
        id = id,
        nombre = nombre,
        comercio = negocios?.nombre.orEmpty(),
        categoria = Categoria.valueOf(categoria),
        descripcion = descripcion,
        pesoKg = pesoKg,
        precioNormal = precioNormal,
        precioRescate = precioRescate,
        cantidadDisponible = cantidadDisponible,
        horaRetiroDesde = horaRetiroDesde.take(5),
        horaRetiroHasta = horaRetiroHasta.take(5),
        imagenUri = imagenUrl,
        propietarioId = negocios?.propietarioId,
        estadoPublicacion = estado,
        fechaRetiro = LocalDate.parse(fechaRetiro)
    )
}

data class NegocioOfertaDto(
    val nombre: String,
    @SerializedName("propietario_id") val propietarioId: String?
)

fun Oferta.toCampos(): Map<String, Any?> = mapOf(
    "nombre" to nombre,
    "categoria" to categoria.name,
    "descripcion" to descripcion,
    "peso_kg" to pesoKg,
    "precio_normal" to precioNormal,
    "precio_rescate" to precioRescate,
    "cantidad_disponible" to cantidadDisponible,
    "fecha_retiro" to fechaRetiro.toString(),
    "hora_retiro_desde" to horaRetiroDesde,
    "hora_retiro_hasta" to horaRetiroHasta,
    "imagen_url" to imagenUri
)
