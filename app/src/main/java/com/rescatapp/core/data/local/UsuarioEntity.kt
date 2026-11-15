package com.rescatapp.core.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.features.auth.domain.model.Usuario

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["correo"], unique = true)
    ]
)
data class UsuarioEntity(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val correo: String,
    val contrasenaHash: String,
    val rol: RolUsuario,
    val telefono: String = "",
    val direccion: String = "",
    val nombreNegocio: String? = null,
    val fechaCreacion: Long = System.currentTimeMillis()
)

fun UsuarioEntity.toDomain(): Usuario = Usuario(
    id = id,
    nombre = nombre,
    correo = correo,
    contrasenaHash = contrasenaHash,
    rol = rol,
    telefono = telefono,
    direccion = direccion,
    nombreNegocio = nombreNegocio,
    fechaCreacion = fechaCreacion
)

fun Usuario.toEntity(): UsuarioEntity = UsuarioEntity(
    id = id,
    nombre = nombre,
    correo = correo,
    contrasenaHash = contrasenaHash,
    rol = rol,
    telefono = telefono,
    direccion = direccion,
    nombreNegocio = nombreNegocio,
    fechaCreacion = fechaCreacion
)
