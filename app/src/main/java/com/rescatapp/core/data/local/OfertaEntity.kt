package com.rescatapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta

@Entity(tableName = "ofertas")
data class OfertaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val comercio: String,
    val categoria: Categoria,
    val descripcion: String,
    val pesoKg: Double,
    val precioNormal: Double,
    val precioRescate: Double,
    val cantidadDisponible: Int,
    val horaRetiroDesde: String,
    val horaRetiroHasta: String,
    val imagenUri: String? = null
) {
    fun toDomain(): Oferta = Oferta(
        id = id,
        nombre = nombre,
        comercio = comercio,
        categoria = categoria,
        descripcion = descripcion,
        pesoKg = pesoKg,
        precioNormal = precioNormal,
        precioRescate = precioRescate,
        cantidadDisponible = cantidadDisponible,
        horaRetiroDesde = horaRetiroDesde,
        horaRetiroHasta = horaRetiroHasta,
        imagenUri = imagenUri
    )
}

fun Oferta.toEntity(): OfertaEntity = OfertaEntity(
    id = id,
    nombre = nombre,
    comercio = comercio,
    categoria = categoria,
    descripcion = descripcion,
    pesoKg = pesoKg,
    precioNormal = precioNormal,
    precioRescate = precioRescate,
    cantidadDisponible = cantidadDisponible,
    horaRetiroDesde = horaRetiroDesde,
    horaRetiroHasta = horaRetiroHasta,
    imagenUri = imagenUri
)
