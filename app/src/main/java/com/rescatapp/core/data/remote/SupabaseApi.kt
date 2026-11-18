package com.rescatapp.core.data.remote

import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@JvmSuppressWildcards
interface SupabaseApi {

    @POST("auth/v1/token?grant_type=password")
    suspend fun iniciarSesion(@Body datos: Map<String, String>): TokenDto

    @POST("auth/v1/token?grant_type=refresh_token")
    suspend fun renovarToken(@Body datos: Map<String, String>): TokenDto

    @POST("auth/v1/signup")
    suspend fun registrarUsuario(@Body datos: Map<String, Any>): TokenDto

    @GET("rest/v1/perfiles?select=nombre,rol,direccion")
    suspend fun obtenerPerfil(
        @Header("Authorization") token: String,
        @Query("id") id: String
    ): List<PerfilDto>

    @GET("rest/v1/negocios?select=id,nombre")
    suspend fun obtenerNegocio(
        @Header("Authorization") token: String,
        @Query("propietario_id") propietarioId: String
    ): List<NegocioDto>

    @GET(
        "rest/v1/ofertas?select=*,negocios(nombre,propietario_id)&estado=neq.ELIMINADA&order=id.desc"
    )
    suspend fun obtenerOfertas(@Header("Authorization") token: String?): List<OfertaDto>

    @POST("rest/v1/ofertas")
    suspend fun crearOferta(@Header("Authorization") token: String, @Body oferta: Map<String, Any?>)

    @PATCH("rest/v1/ofertas")
    suspend fun editarOferta(
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Body oferta: Map<String, Any?>
    )

    @POST("rest/v1/rpc/eliminar_oferta")
    suspend fun eliminarOferta(
        @Header("Authorization") token: String,
        @Body datos: Map<String, Any>
    )

    @POST("rest/v1/rpc/cambiar_estado_oferta")
    suspend fun cambiarEstadoOferta(
        @Header("Authorization") token: String,
        @Body datos: Map<String, Any>
    )

    @POST("storage/v1/object/ofertas/{ruta}")
    suspend fun subirImagen(
        @Header("Authorization") token: String,
        @Path("ruta", encoded = true) ruta: String,
        @Body imagen: RequestBody
    )

    @GET("rest/v1/pedidos?select=*&order=creado_at.desc")
    suspend fun obtenerPedidos(@Header("Authorization") token: String): List<PedidoDto>

    @POST("rest/v1/rpc/reservar_oferta")
    suspend fun reservarOferta(
        @Header("Authorization") token: String,
        @Body datos: Map<String, Any>
    ): List<PedidoDto>

    @POST("rest/v1/rpc/cancelar_pedido")
    suspend fun cancelarPedido(
        @Header("Authorization") token: String,
        @Body datos: Map<String, Any>
    ): List<PedidoDto>

    @POST("rest/v1/rpc/avanzar_pedido")
    suspend fun avanzarPedido(
        @Header("Authorization") token: String,
        @Body datos: Map<String, Any?>
    ): List<PedidoDto>

    @GET("rest/v1/notificaciones?select=*&order=creado_at.desc")
    suspend fun obtenerNotificaciones(@Header("Authorization") token: String): List<NotificacionDto>

    @PATCH("rest/v1/notificaciones")
    suspend fun marcarNotificacionesLeidas(
        @Header("Authorization") token: String,
        @Query("destinatario_id") destinatarioId: String,
        @Query("id") id: String?,
        @Body datos: Map<String, Boolean> = mapOf("leida" to true)
    )

    @DELETE("rest/v1/notificaciones")
    suspend fun eliminarNotificaciones(
        @Header("Authorization") token: String,
        @Query("destinatario_id") destinatarioId: String,
        @Query("id") id: String?
    )
}
