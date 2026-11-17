package com.rescatapp.core.data.supabase

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParser
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ErrorSupabase(val codigo: Int, mensaje: String) : IOException(mensaje)

/** REST de Supabase: apikey publica y JWT del usuario; nunca service_role. */
class SupabaseHttp(
    private val url: String,
    private val clavePublica: String,
    private val cliente: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .callTimeout(25, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .followRedirects(false)
        .build()
) {
    private val gson = Gson()

    suspend fun ejecutar(
        ruta: String,
        metodo: String = "GET",
        datos: Any? = null,
        token: String? = null,
        archivo: ByteArray? = null,
        mime: String = "application/json"
    ): JsonElement = withContext(Dispatchers.IO) {
        require(ruta.startsWith("/") && !ruta.startsWith("//"))
        val cuerpo = when {
            archivo != null -> archivo.toRequestBody(mime.toMediaType())

            metodo != "GET" && metodo != "HEAD" -> gson.toJson(
                datos ?: emptyMap<String, String>()
            ).toRequestBody("application/json".toMediaType())

            else -> null
        }
        val solicitud = Request.Builder().url(url.trimEnd('/') + ruta)
            .header("apikey", clavePublica)
            .header("Prefer", "return=representation")
            .apply { if (token != null) header("Authorization", "Bearer $token") }
            .method(metodo, cuerpo).build()
        cliente.newCall(solicitud).execute().use { respuesta ->
            val texto = respuesta.body?.string().orEmpty()
            if (!respuesta.isSuccessful) {
                val mensaje = runCatching {
                    val json = JsonParser.parseString(texto).asJsonObject
                    json.texto("msg").ifBlank {
                        json.texto("message")
                    }.ifBlank { json.texto("error_description") }
                }.getOrDefault("")
                throw ErrorSupabase(
                    respuesta.code,
                    mensaje.ifBlank {
                        "No se pudo completar la solicitud (${respuesta.code})."
                    }
                )
            }
            if (texto.isBlank()) JsonNull.INSTANCE else JsonParser.parseString(texto)
        }
    }
}
