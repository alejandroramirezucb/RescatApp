package com.rescatapp.navigation

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.navigation.ArgumentosRuta

object Rutas {
    const val INICIO = "inicio"
    const val EXPLORAR = "explorar"
    const val EXPLORAR_POR_CATEGORIA =
        "$EXPLORAR?${ArgumentosRuta.CATEGORIA}={${ArgumentosRuta.CATEGORIA}}"
    const val DETALLE = "detalle/{${ArgumentosRuta.OFERTA_ID}}"
    const val REGISTRO = "registro"
    const val PEDIDOS = "pedidos"
    const val PERFIL = "perfil"
    const val OFERTAS_NEGOCIO = "ofertas_negocio"

    fun explorar(categoria: Categoria?): String =
        categoria?.let { "$EXPLORAR?${ArgumentosRuta.CATEGORIA}=${it.name}" } ?: EXPLORAR

    fun detalle(ofertaId: Int): String = "detalle/$ofertaId"
}
