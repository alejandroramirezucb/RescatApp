package com.rescatapp.core.data.remote

import com.google.gson.annotations.SerializedName

data class TokenDto(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("expires_in") val expiraEn: Long?,
    val user: UsuarioDto?
)

data class UsuarioDto(val id: String, val email: String)

data class PerfilDto(val nombre: String, val rol: String, val direccion: String = "")

data class NegocioDto(val id: Int, val nombre: String)
