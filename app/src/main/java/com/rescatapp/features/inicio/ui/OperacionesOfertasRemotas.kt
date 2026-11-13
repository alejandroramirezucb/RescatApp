package com.rescatapp.features.inicio.ui

import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import com.rescatapp.core.data.remote.FuenteOfertasRemotas
import com.rescatapp.core.model.Oferta
import java.io.EOFException
import java.io.IOException
import retrofit2.HttpException

internal suspend fun FuenteOfertasRemotas.aplicarOperacion(
    operacion: OperacionOferta,
    ofertas: List<Oferta>
): List<Oferta> {
    val oferta = operacion.oferta
    return when (operacion.tipo) {
        TipoOperacionOferta.PUBLICAR -> {
            val publicada = crearOferta(oferta)
            listOf(publicada) + ofertas.filterNot { it.id == publicada.id }
        }

        TipoOperacionOferta.EDITAR -> {
            val actualizada = editarOferta(oferta.id, oferta)
            ofertas.map { if (it.id == oferta.id) actualizada else it }
        }

        TipoOperacionOferta.ELIMINAR -> {
            eliminarOferta(oferta.id)
            ofertas.filterNot { it.id == oferta.id }
        }
    }
}

internal fun mensajeDeExito(tipo: TipoOperacionOferta): String = when (tipo) {
    TipoOperacionOferta.PUBLICAR -> "Oferta publicada en línea."
    TipoOperacionOferta.EDITAR -> "Cambios guardados en línea."
    TipoOperacionOferta.ELIMINAR -> "Oferta eliminada de la lista en línea."
}

internal fun mensajeDeError(error: Exception): String = when (error) {
    is HttpException -> "El servidor no pudo completar la operación. Inténtalo de nuevo."

    is JsonParseException, is MalformedJsonException, is EOFException,
    is IllegalArgumentException, is NullPointerException ->
        "No pudimos interpretar las ofertas recibidas."

    is IOException -> "No pudimos conectarnos. Revisa tu conexión e inténtalo de nuevo."

    else -> "No se pudo completar la operación. Inténtalo de nuevo."
}
