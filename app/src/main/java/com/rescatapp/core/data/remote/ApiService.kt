package com.rescatapp.core.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("ofertas")
    suspend fun obtenerOfertas(): List<OfertaDto>

    @POST("ofertas")
    suspend fun crearOferta(@Body oferta: OfertaDto): OfertaDto

    @PUT("ofertas/{id}")
    suspend fun actualizarOferta(@Path("id") id: String, @Body oferta: OfertaDto): OfertaDto

    @DELETE("ofertas/{id}")
    suspend fun eliminarOferta(@Path("id") id: String)
}
