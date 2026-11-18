package com.rescatapp.core.data

import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.OfertaEntity
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.Oferta
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class OfertaDaoFalso(ofertasIniciales: List<Oferta> = ofertasDeEjemplo) : OfertaDao {
    val entidades = MutableStateFlow(ofertasIniciales.map { it.toEntity() })

    override suspend fun insertarTodas(ofertas: List<OfertaEntity>) {
        entidades.update { lista ->
            ofertas +
                lista.filterNot { item -> ofertas.any { it.id == item.id } }
        }
    }

    override suspend fun eliminarExcepto(ids: List<Int>) {
        entidades.update { lista -> lista.filter { it.id in ids } }
    }

    override fun observarTodas(): Flow<List<OfertaEntity>> =
        entidades.map { lista -> lista.sortedByDescending { it.id } }

    override fun filtrar(categoria: String?, texto: String): Flow<List<OfertaEntity>> =
        observarTodas().map { lista ->
            lista.filter {
                it.estadoPublicacion == "ACTIVA" && it.cantidadDisponible > 0 &&
                    (categoria == null || it.categoria.name == categoria) &&
                    (it.nombre.contains(texto, true) || it.comercio.contains(texto, true))
            }
        }
}
