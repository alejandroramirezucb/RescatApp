package com.rescatapp.core.data.remote

import com.google.gson.JsonParser
import java.io.IOException
import retrofit2.HttpException

fun mensajeDeError(error: Exception): String = when (error) {
    is HttpException -> mensajeDelServidor(error)
    is IOException -> "No pudimos conectarnos. Revisa tu conexión e intenta nuevamente."
    else -> error.message ?: "No se pudo completar la operación."
}

private fun mensajeDelServidor(error: HttpException): String {
    val cuerpo = error.response()?.errorBody()?.string().orEmpty()
    val mensaje = runCatching {
        val json = JsonParser.parseString(cuerpo).asJsonObject
        listOf("msg", "message", "error_description")
            .firstNotNullOfOrNull { json.get(it)?.asString }
    }.getOrNull().orEmpty()
    return when {
        mensaje.contains("Invalid login credentials") -> "Correo o contraseña incorrectos."
        mensaje.contains("Email not confirmed") -> "Confirma tu correo antes de iniciar sesión."
        mensaje.contains("already registered") -> "Ya existe una cuenta con este correo."
        error.code() == 401 -> "Tu sesión venció. Vuelve a iniciar sesión."
        error.code() == 429 -> "Demasiados intentos. Espera un momento antes de reintentar."
        mensaje.isNotBlank() -> mensaje
        else -> "El servidor no pudo completar la operación."
    }
}
