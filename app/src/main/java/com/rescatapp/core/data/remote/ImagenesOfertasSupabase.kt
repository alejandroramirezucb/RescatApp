package com.rescatapp.core.data.remote

import android.content.Context
import android.net.Uri
import com.rescatapp.BuildConfig
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

interface ImagenesOfertas {
    suspend fun subir(ruta: String?, usuarioId: String, token: String): String?
}

class ImagenesOfertasSupabase @Inject constructor(
    @param:ApplicationContext private val contexto: Context,
    private val api: SupabaseApi
) : ImagenesOfertas {
    override suspend fun subir(ruta: String?, usuarioId: String, token: String): String? {
        if (ruta == null || !ruta.startsWith("content://")) return ruta
        val uri = Uri.parse(ruta)
        val tipoCrudo = contexto.contentResolver.getType(uri) ?: "image/jpeg"
        val tipo = if (tipoCrudo.equals("image/jpg", ignoreCase = true)) {
            "image/jpeg"
        } else {
            tipoCrudo.lowercase()
        }
        val extension = EXTENSIONES_PERMITIDAS[tipo]
            ?: throw IllegalArgumentException("Solo se permiten fotografías JPG, PNG o WebP.")
        val bytes = withContext(Dispatchers.IO) {
            contexto.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } ?: error("No se pudo leer la fotografía.")
        require(bytes.size <= TAMANO_MAXIMO) { "La fotografía supera los 5 MB." }
        val destino = "$usuarioId/${UUID.randomUUID()}.$extension"
        api.subirImagen(token, destino, bytes.toRequestBody(tipo.toMediaType()))
        return "${BuildConfig.SUPABASE_URL.trimEnd('/')}/storage/v1/object/public/ofertas/$destino"
    }

    private companion object {
        const val TAMANO_MAXIMO = 5 * 1024 * 1024
        val EXTENSIONES_PERMITIDAS = mapOf(
            "image/jpeg" to "jpg",
            "image/png" to "png",
            "image/webp" to "webp"
        )
    }
}
