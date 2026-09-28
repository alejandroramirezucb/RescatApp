package com.rescatapp.core.data.repository

import com.rescatapp.core.data.local.dao.OfertaDao
import com.rescatapp.core.data.local.entity.toDomainModel
import com.rescatapp.core.data.local.entity.toEntity
import com.rescatapp.core.domain.repository.OfertasRepository
import com.rescatapp.core.model.Oferta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfertasRepositoryImpl @Inject constructor(
    private val ofertaDao: OfertaDao
) : OfertasRepository {

    override fun getOfertas(): Flow<List<Oferta>> {
        return ofertaDao.getOfertasStream().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun updateOferta(oferta: Oferta) {
        val currentEntity = ofertaDao.getOfertaById(oferta.id)
        val imageUrl = currentEntity?.imageUrl ?: ""
        ofertaDao.updateOferta(oferta.toEntity(imageUrl))
    }
}
