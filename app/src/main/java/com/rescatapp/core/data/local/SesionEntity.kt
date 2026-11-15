package com.rescatapp.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.SesionUsuario

@Entity(tableName = "sesion_activa")
data class SesionEntity(
    @PrimaryKey
    val id: Int = ID_SESION_UNICA,
    val usuarioId: String,
    val correo: String,
    val nombre: String,
    val rol: RolUsuario,
    val nombreNegocio: String? = null,
    val fechaInicio: Long = System.currentTimeMillis(),
    val token: String = ""
) {
    fun toDomain(): SesionUsuario = SesionUsuario(
        usuarioId = usuarioId,
        nombre = nombre,
        correo = correo,
        rol = rol,
        nombreNegocio = nombreNegocio,
        token = token,
        fechaInicio = fechaInicio
    )

    companion object {
        const val ID_SESION_UNICA = 1
    }
}

fun SesionUsuario.toEntity(): SesionEntity = SesionEntity(
    id = SesionEntity.ID_SESION_UNICA,
    usuarioId = usuarioId,
    correo = correo,
    nombre = nombre,
    rol = rol,
    nombreNegocio = nombreNegocio,
    token = token,
    fechaInicio = fechaInicio
)
