package com.rescatapp.core.domain.repository

import com.rescatapp.core.model.Oferta
import kotlinx.coroutines.flow.Flow

interface OfertasRepository {
    fun getOfertas(): Flow<List<Oferta>>
    suspend fun updateOferta(oferta: Oferta)
}
