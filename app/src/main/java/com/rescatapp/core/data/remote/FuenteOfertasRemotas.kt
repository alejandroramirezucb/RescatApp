package com.rescatapp.core.data.remote

import com.rescatapp.core.model.Oferta

interface FuenteOfertasRemotas {
    suspend fun obtenerOfertasRemotas(): List<Oferta>
    suspend fun crearOferta(oferta: Oferta): Oferta
    suspend fun editarOferta(id: Int, ofertaActualizada: Oferta): Oferta
    suspend fun eliminarOferta(id: Int)
}
