package com.rescatapp.core.data

import com.rescatapp.core.data.remote.NegocioDto
import com.rescatapp.core.data.remote.PedidoDto
import com.rescatapp.core.data.remote.PerfilDto
import com.rescatapp.core.data.remote.SupabaseApi
import com.rescatapp.core.data.remote.TokenDto
import com.rescatapp.core.data.remote.UsuarioDto
import com.rescatapp.core.model.EstadoPagoSimulado
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.Pedido
import kotlinx.coroutines.CompletableDeferred
import okhttp3.RequestBody

class SupabaseApiFalsa(
    ofertas: List<Oferta> = ofertasDeEjemplo,
    pedidos: List<Pedido> = emptyList(),
    notificaciones: List<Notificacion> = emptyList()
) : SupabaseApi {
    val ofertas = ofertas.toMutableList()
    val pedidos = pedidos.toMutableList()
    val notificaciones = notificaciones.toMutableList()
    val ofertasCreadas = mutableListOf<Map<String, Any?>>()
    var negocios = listOf(NegocioDto(1, "Panadería La Central"))
    var perfil = PerfilDto("Ana Pérez", "CLIENTE")
    var confirmarCorreo = false
    var fallo: Exception? = null
    var falloSincronizarPedidos: Exception? = null
    var cancelacionesSolicitadas = 0
    var esperaCancelacion: CompletableDeferred<Unit>? = null
    var esperaCreacion: CompletableDeferred<Unit>? = null

    override suspend fun iniciarSesion(datos: Map<String, String>) = responder { token() }

    override suspend fun renovarToken(datos: Map<String, String>) = responder { token() }

    override suspend fun registrarUsuario(datos: Map<String, Any>) = responder {
        if (confirmarCorreo) TokenDto(null, null, null, null) else token()
    }

    override suspend fun obtenerPerfil(token: String, id: String) = responder { listOf(perfil) }

    override suspend fun obtenerNegocio(token: String, propietarioId: String) =
        responder { negocios }

    override suspend fun obtenerOfertas(token: String?) = responder { ofertas.map { it.aDto() } }

    override suspend fun crearOferta(token: String, oferta: Map<String, Any?>) {
        esperaCreacion?.await()
        responder {
            ofertasCreadas.add(oferta)
            val nueva = ofertasDeEjemplo.first().copy(
                id = (ofertas.maxOfOrNull { it.id } ?: 0) + 1,
                propietarioId = USUARIO_PRUEBA
            )
            ofertas.add(nueva.conCampos(oferta))
        }
    }

    override suspend fun editarOferta(token: String, id: String, oferta: Map<String, Any?>) =
        responder {
            reemplazarOferta(id.removePrefix("eq.").toInt()) { it.conCampos(oferta) }
        }

    override suspend fun eliminarOferta(token: String, datos: Map<String, Any>) = responder {
        ofertas.removeAll { it.id == datos["p_oferta_id"] }
        Unit
    }

    override suspend fun cambiarEstadoOferta(token: String, datos: Map<String, Any>) = responder {
        reemplazarOferta(datos["p_oferta_id"] as Int) {
            it.copy(estadoPublicacion = datos["p_estado"] as String)
        }
    }

    override suspend fun subirImagen(token: String, ruta: String, imagen: RequestBody) = Unit

    override suspend fun obtenerPedidos(token: String) = responder {
        falloSincronizarPedidos?.let { throw it }
        pedidos.map { it.aDto() }
    }

    override suspend fun reservarOferta(token: String, datos: Map<String, Any>) = responder {
        val oferta = ofertas.first { it.id == datos["p_oferta_id"] }
        if (oferta.estaAgotada) throw errorServidor("Esta oferta está agotada")
        reemplazarOferta(oferta.id) { it.copy(cantidadDisponible = it.cantidadDisponible - 1) }
        guardarPedido(oferta.crearPedido((pedidos.maxOfOrNull { it.id } ?: 0) + 1))
    }

    override suspend fun cancelarPedido(token: String, datos: Map<String, Any>): List<PedidoDto> {
        cancelacionesSolicitadas++
        esperaCancelacion?.await()
        return responder {
            val pedido = pedidos.first { it.id == datos["p_pedido_id"] }
            if (pedido.estado == EstadoPedido.CANCELADO) return@responder listOf(pedido.aDto())
            if (pedido.estado != EstadoPedido.RESERVADO) {
                throw errorServidor("Solo puedes cancelar un pedido reservado")
            }
            reemplazarOferta(pedido.ofertaId) {
                it.copy(cantidadDisponible = it.cantidadDisponible + 1)
            }
            guardarPedido(
                pedido.copy(
                    estado = EstadoPedido.CANCELADO,
                    estadoPagoSimulado = EstadoPagoSimulado.ANULADO
                )
            )
        }
    }

    override suspend fun avanzarPedido(token: String, datos: Map<String, Any?>) = responder {
        val pedido = pedidos.first { it.id == datos["p_pedido_id"] }
        val siguiente = pedido.estado.siguiente()
            ?: throw errorServidor("El pedido ya está ${pedido.estado.etiqueta}")
        guardarPedido(pedido.copy(estado = siguiente))
    }

    override suspend fun obtenerNotificaciones(token: String) =
        responder { notificaciones.map { it.aDto() } }

    override suspend fun marcarNotificacionesLeidas(
        token: String,
        destinatarioId: String,
        id: String?,
        datos: Map<String, Boolean>
    ) = responder {
        notificaciones.replaceAll {
            if (id == null ||
                "eq.${it.id}" == id
            ) {
                it.copy(leida = true)
            } else {
                it
            }
        }
    }

    override suspend fun eliminarNotificaciones(
        token: String,
        destinatarioId: String,
        id: String?
    ) = responder {
        notificaciones.removeAll { id == null || "eq.${it.id}" == id }
        Unit
    }

    private fun <T> responder(respuesta: () -> T): T {
        fallo?.let { throw it }
        return respuesta()
    }

    private fun token() =
        TokenDto("acceso", "renovacion", 3600, UsuarioDto(USUARIO_PRUEBA, "ana@correo.com"))

    private fun reemplazarOferta(id: Int, cambio: (Oferta) -> Oferta) {
        ofertas.replaceAll { if (it.id == id) cambio(it) else it }
    }

    private fun guardarPedido(pedido: Pedido): List<PedidoDto> {
        pedidos.removeAll { it.id == pedido.id }
        pedidos.add(0, pedido)
        return listOf(pedido.aDto())
    }
}
