package com.rescatapp.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta

@Entity(tableName = "ofertas")
data class OfertaEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val comercio: String,
    val categoria: String, // Usaremos un converter o mapearemos a String manual (name del enum)
    val descripcion: String,
    val pesoKg: Double,
    val precioNormal: Double,
    val precioRescate: Double,
    val cantidadDisponible: Int,
    val horaRetiroDesde: String,
    val horaRetiroHasta: String,
    val imageUrl: String // Para coil, extra data no en modelo base inicial pero útil para la UI mockeada
)

fun OfertaEntity.toDomainModel(): Oferta {
    return Oferta(
        id = id,
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
}

fun Oferta.toEntity(imageUrl: String = ""): OfertaEntity {
    return OfertaEntity(
        id = id,
        nombre = nombre,
        comercio = comercio,
        categoria = categoria.name,
        descripcion = descripcion,
        pesoKg = pesoKg,
        precioNormal = precioNormal,
        precioRescate = precioRescate,
        cantidadDisponible = cantidadDisponible,
        horaRetiroDesde = horaRetiroDesde,
        horaRetiroHasta = horaRetiroHasta,
        imageUrl = imageUrl
    )
}
