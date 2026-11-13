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
    private val entidades = MutableStateFlow(ofertasIniciales.map { it.toEntity() })
    private var siguienteId = (ofertasIniciales.maxOfOrNull { it.id } ?: 0) + 1

    override suspend fun insertar(oferta: OfertaEntity): Long {
        val guardada = if (oferta.id == 0) oferta.copy(id = siguienteId++) else oferta
        siguienteId = maxOf(siguienteId, guardada.id + 1)
        entidades.update { lista ->
            (lista.filterNot { it.id == guardada.id } + guardada).sortedByDescending { it.id }
        }
        return guardada.id.toLong()
    }

    override suspend fun insertarTodas(ofertas: List<OfertaEntity>) {
        ofertas.forEach { insertar(it) }
    }

    override suspend fun actualizar(oferta: OfertaEntity) {
        entidades.update { lista -> lista.map { if (it.id == oferta.id) oferta else it } }
    }

    override suspend fun eliminar(oferta: OfertaEntity) {
        eliminarPorId(oferta.id)
    }

    override suspend fun eliminarPorId(id: Int): Int {
        val existia = entidades.value.any { it.id == id }
        entidades.update { lista -> lista.filterNot { it.id == id } }
        return if (existia) 1 else 0
    }

    override fun observarTodas(): Flow<List<OfertaEntity>> =
        entidades.map { lista -> lista.sortedByDescending { it.id } }

    override fun observarPorId(id: Int): Flow<OfertaEntity?> =
        entidades.map { lista -> lista.find { it.id == id } }

    override suspend fun buscarPorId(id: Int): OfertaEntity? =
        entidades.value.find { it.id == id }

    override suspend fun cambiarUnidadesDisponibles(id: Int, diferencia: Int): Int {
        val oferta = buscarPorId(id) ?: return 0
        if (oferta.cantidadDisponible + diferencia < 0) return 0
        actualizar(oferta.copy(cantidadDisponible = oferta.cantidadDisponible + diferencia))
        return 1
    }

    override fun filtrar(categoria: String?, texto: String?): Flow<List<OfertaEntity>> =
        observarTodas().map { lista ->
            lista.filter { oferta ->
                (categoria == null || oferta.categoria.name == categoria) &&
                    (texto.isNullOrEmpty() || oferta.nombre.contains(texto, ignoreCase = true) ||
                        oferta.comercio.contains(texto, ignoreCase = true))
            }
        }
}

fun crearRepositorioOfertas(ofertasIniciales: List<Oferta> = ofertasDeEjemplo) =
    RepositorioOfertas(OfertaDaoFalso(ofertasIniciales))
