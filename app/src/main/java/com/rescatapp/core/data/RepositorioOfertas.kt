package com.rescatapp.core.data

import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.data.remote.ImagenesOfertas
import com.rescatapp.core.data.remote.SupabaseApi
import com.rescatapp.core.data.remote.toCampos
import com.rescatapp.core.domain.sigueVigente
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

@Singleton
class RepositorioOfertas @Inject constructor(
    private val api: SupabaseApi,
    private val ofertaDao: OfertaDao,
    private val autenticacion: RepositorioAutenticacion,
    private val imagenes: ImagenesOfertas
) {
    val ofertas: Flow<List<Oferta>> = ofertaDao.observarTodas().map { entidades ->
        entidades.map { it.toDomain() }
    }

    val propias: Flow<List<Oferta>> =
        combine(ofertas, autenticacion.sesionActual) { lista, sesion ->
            lista.filter { it.propietarioId == sesion?.usuarioId }
        }

    val catalogo: Flow<List<Oferta>> = filtrar(categoria = null, texto = "")

    fun obtenerPorId(id: Int): Flow<Oferta?> = ofertas.map { lista -> lista.find { it.id == id } }

    fun filtrar(categoria: Categoria?, texto: String): Flow<List<Oferta>> =
        ofertaDao.filtrar(categoria?.name, texto.trim()).map { entidades ->
            entidades.map { it.toDomain() }.filter { it.sigueVigente() }
        }

    suspend fun sincronizar() {
        val remotas = api.obtenerOfertas(autenticacion.token()).map { it.toDomain() }
        ofertaDao.insertarTodas(remotas.map { it.toEntity() })
        ofertaDao.eliminarExcepto(remotas.map { it.id })
    }

    suspend fun agregar(oferta: Oferta) = operar { token, usuarioId ->
        val negocio = api.obtenerNegocio(token, "eq.$usuarioId").firstOrNull()
            ?: throw NoSuchElementException("No encontramos un negocio asociado a tu cuenta.")
        val imagen = imagenes.subir(oferta.imagenUri, usuarioId, token)
        api.crearOferta(
            token,
            oferta.copy(imagenUri = imagen).toCampos() + ("negocio_id" to negocio.id)
        )
    }

    suspend fun actualizar(oferta: Oferta) = operar { token, usuarioId ->
        val imagen = imagenes.subir(oferta.imagenUri, usuarioId, token)
        api.editarOferta(token, "eq.${oferta.id}", oferta.copy(imagenUri = imagen).toCampos())
    }

    suspend fun eliminar(oferta: Oferta) = operar { token, _ ->
        api.eliminarOferta(token, mapOf("p_oferta_id" to oferta.id))
    }

    suspend fun cambiarEstado(oferta: Oferta) = operar { token, _ ->
        val nuevoEstado = if (oferta.estadoPublicacion == "PAUSADA") "ACTIVA" else "PAUSADA"
        api.cambiarEstadoOferta(token, mapOf("p_oferta_id" to oferta.id, "p_estado" to nuevoEstado))
    }

    private suspend fun operar(accion: suspend (token: String, usuarioId: String) -> Unit) {
        val token = autenticacion.tokenObligatorio()
        accion(token, checkNotNull(autenticacion.sesion()).usuarioId)
        try {
            sincronizar()
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (_: Exception) {
        }
    }
}
