package com.rescatapp.core.data.remote

import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositorioOfertasRemotas(private val api: ApiService) {
    @Inject
    constructor() : this(RetrofitClient.api)

    suspend fun obtenerOfertasRemotas(): List<Oferta> = api.obtenerOfertas().map { it.aOferta() }

    suspend fun crearOferta(oferta: Oferta): Oferta = api.crearOferta(oferta.aOfertaDto()).aOferta()

    suspend fun editarOferta(id: Int, ofertaActualizada: Oferta): Oferta =
        api.actualizarOferta(id.toString(), ofertaActualizada.aOfertaDto()).aOferta()

    suspend fun eliminarOferta(id: Int) {
        api.eliminarOferta(id.toString())
    }
}
