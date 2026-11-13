package com.rescatapp.core.data

import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.filtrar
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RepositorioOfertas @Inject constructor(private val ofertaDao: OfertaDao) {
    val ofertas: Flow<List<Oferta>> = ofertaDao.observarTodas().map { entidades ->
        entidades.map { it.toDomain() }
    }

    fun obtenerPorId(id: Int): Flow<Oferta?> = ofertaDao.observarPorId(id).map { it?.toDomain() }

    suspend fun buscarPorId(id: Int): Oferta? = ofertaDao.buscarPorId(id)?.toDomain()

    suspend fun agregar(oferta: Oferta): Oferta {
        val id = ofertaDao.insertar(oferta.copy(id = 0).toEntity())
        return oferta.copy(id = id.toInt())
    }

    suspend fun actualizar(oferta: Oferta) {
        ofertaDao.actualizar(oferta.toEntity())
    }

    suspend fun eliminar(ofertaId: Int) {
        ofertaDao.eliminarPorId(ofertaId)
    }

    suspend fun cambiarUnidadesDisponibles(ofertaId: Int, diferencia: Int): Boolean =
        ofertaDao.cambiarUnidadesDisponibles(ofertaId, diferencia) > 0

    fun filtrar(categoria: Categoria?, texto: String): Flow<List<Oferta>> =
        ofertaDao.filtrar(categoria, texto.trim()).map { entidades ->
            entidades.map { it.toDomain() }
        }
}
