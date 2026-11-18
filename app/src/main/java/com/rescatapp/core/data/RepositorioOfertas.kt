package com.rescatapp.core.data

import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.filtrar
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.data.supabase.SupabaseConexion
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import com.rescatapp.core.model.UsuarioDemo
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class RepositorioOfertas @Inject constructor(
    private val ofertaDao: OfertaDao,
    private val supabase: SupabaseConexion? = null
) {
    val conectado: Boolean get() = supabase != null
    val ofertas: Flow<List<Oferta>> =
        supabase?.ofertas ?: ofertaDao.observarTodas().map { entidades ->
            entidades.map { it.toDomain() }
        }
    val propias: Flow<List<Oferta>> = ofertas.map { lista ->
        if (supabase != null) {
            lista.filter { it.propietarioId == supabase.sesion.value?.usuarioId }
        } else {
            lista.filter { it.comercio == UsuarioDemo.NEGOCIO_DEMO }
        }
    }
    val catalogo: Flow<List<Oferta>> = if (supabase != null) {
        ofertas.map { lista ->
            lista.filter(SupabaseConexion::visibleEnCatalogo)
        }
    } else {
        ofertas
    }

    fun obtenerPorId(id: Int): Flow<Oferta?> = if (supabase !=
        null
    ) {
        ofertas.map { lista -> lista.find { it.id == id } }
    } else {
        ofertaDao.observarPorId(id).map { it?.toDomain() }
    }

    suspend fun buscarPorId(id: Int): Oferta? = if (supabase !=
        null
    ) {
        ofertas.first().find { it.id == id }
    } else {
        ofertaDao.buscarPorId(id)?.toDomain()
    }

    suspend fun agregar(oferta: Oferta): Oferta {
        if (supabase != null) return supabase.guardarOferta(oferta, nueva = true)
        val id = ofertaDao.insertar(oferta.copy(id = 0).toEntity())
        return oferta.copy(id = id.toInt())
    }

    suspend fun actualizar(oferta: Oferta) {
        if (supabase != null) {
            supabase.guardarOferta(oferta, nueva = false)
            return
        }
        ofertaDao.actualizar(oferta.toEntity())
    }

    suspend fun eliminar(oferta: Oferta) {
        if (supabase != null) {
            supabase.eliminarOferta(oferta.id)
            return
        }
        ofertaDao.eliminar(oferta.toEntity())
    }

    suspend fun cambiarUnidadesDisponibles(ofertaId: Int, diferencia: Int): Boolean {
        check(supabase == null) { "El servidor administra el stock de las reservas." }
        return ofertaDao.cambiarUnidadesDisponibles(ofertaId, diferencia) > 0
    }

    fun filtrar(categoria: Categoria?, texto: String): Flow<List<Oferta>> = if (supabase != null) {
        catalogo.map { lista ->
            lista.filter {
                (categoria == null || it.categoria == categoria) &&
                    (
                        it.nombre.contains(texto.trim(), true) ||
                            it.comercio.contains(texto.trim(), true)
                        )
            }
        }
    } else {
        ofertaDao.filtrar(categoria, texto.trim()).map { entidades ->
            entidades.map { it.toDomain() }
        }
    }

    suspend fun cambiarEstado(oferta: Oferta) {
        val nuevoEstado = if (oferta.estadoPublicacion == "PAUSADA") "ACTIVA" else "PAUSADA"
        checkNotNull(supabase).cambiarEstadoOferta(oferta.id, nuevoEstado)
    }
}
