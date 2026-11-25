package com.rescatapp.navigation

import com.rescatapp.core.model.Categoria
import com.rescatapp.core.navigation.ArgumentosRuta
import com.rescatapp.features.explorar.domain.TipoBusquedaExplorar
import java.net.URLEncoder

object Rutas {
    const val INICIO = "inicio"
    const val EXPLORAR = "explorar"
    const val EXPLORAR_POR_CATEGORIA =
        "$EXPLORAR?${ArgumentosRuta.CATEGORIA}={${ArgumentosRuta.CATEGORIA}}" +
            "&${ArgumentosRuta.COMERCIO}={${ArgumentosRuta.COMERCIO}}" +
            "&${ArgumentosRuta.TIPO}={${ArgumentosRuta.TIPO}}"
    const val DETALLE = "detalle/{${ArgumentosRuta.OFERTA_ID}}"
    const val REGISTRO = "registro"
    const val CARRITO = "carrito"
    const val PEDIDOS = "pedidos"
    const val PERFIL = "perfil"
    const val OFERTAS_NEGOCIO = "ofertas_negocio"
    const val NOTIFICACIONES = "notificaciones"
    const val LOGIN = "login"
    const val REGISTRO_USUARIO = "registro_usuario"

    fun explorar(categoria: Categoria?): String =
        categoria?.let { "$EXPLORAR?${ArgumentosRuta.CATEGORIA}=${it.name}" } ?: EXPLORAR

    fun explorarComercio(comercio: String): String {
        val codificado = URLEncoder.encode(comercio, "UTF-8").replace("+", "%20")
        return "$EXPLORAR?${ArgumentosRuta.COMERCIO}=$codificado"
    }

    fun explorarNegocios(): String =
        "$EXPLORAR?${ArgumentosRuta.TIPO}=${TipoBusquedaExplorar.NEGOCIOS.name}"

    fun detalle(ofertaId: Int): String = "detalle/$ofertaId"
}
