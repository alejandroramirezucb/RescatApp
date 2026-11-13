package com.rescatapp.core.data

import com.rescatapp.core.data.local.OfertaDao
import com.rescatapp.core.data.local.OfertaEntity
import com.rescatapp.core.data.local.filtrar
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Singleton
class RepositorioOfertas(
    private val ofertaDao: OfertaDao?,
    private val coroutineScope: CoroutineScope,
    ofertasIniciales: List<Oferta>
) {
    @Inject
    constructor(ofertaDao: OfertaDao) : this(
        ofertaDao = ofertaDao,
        coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        ofertasIniciales = ofertasDeEjemplo
    )

    constructor(ofertasIniciales: List<Oferta> = ofertasDeEjemplo) : this(
        ofertaDao = null,
        coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        ofertasIniciales = ofertasIniciales
    )

    private val ofertasEditables = MutableStateFlow(ofertasIniciales)
    val ofertas: StateFlow<List<Oferta>> = ofertasEditables.asStateFlow()

    init {
        if (ofertaDao != null) {
            coroutineScope.launch {
                ofertaDao.observarTodas().collect { listaEntidades ->
                    if (listaEntidades.isNotEmpty()) {
                        ofertasEditables.value = listaEntidades.map { it.toDomain() }
                    }
                }
            }
        }
    }

    fun obtenerPorId(id: Int): Flow<Oferta?> = if (ofertaDao != null) {
        ofertaDao.observarPorId(id).map { it?.toDomain() }
    } else {
        ofertas.map { lista -> lista.find { it.id == id } }
    }

    fun buscarPorId(id: Int): Oferta? = ofertas.value.find { it.id == id }

    fun agregar(oferta: Oferta): Oferta {
        val ofertaNueva = oferta.copy(id = generarSiguienteId(ofertas.value.map { it.id }))
        ofertasEditables.update { listOf(ofertaNueva) + it }
        if (ofertaDao != null) {
            coroutineScope.launch {
                ofertaDao.insertar(ofertaNueva.toEntity())
            }
        }
        return ofertaNueva
    }

    fun actualizar(oferta: Oferta) {
        ofertasEditables.update { lista -> lista.map { if (it.id == oferta.id) oferta else it } }
        if (ofertaDao != null) {
            coroutineScope.launch {
                ofertaDao.actualizar(oferta.toEntity())
            }
        }
    }

    fun eliminar(ofertaId: Int) {
        ofertasEditables.update { lista -> lista.filter { it.id != ofertaId } }
        if (ofertaDao != null) {
            coroutineScope.launch {
                ofertaDao.eliminarPorId(ofertaId)
            }
        }
    }

    fun cambiarUnidadesDisponibles(ofertaId: Int, diferencia: Int) {
        val oferta = buscarPorId(ofertaId) ?: return
        actualizar(oferta.copy(cantidadDisponible = oferta.cantidadDisponible + diferencia))
    }

    fun filtrar(categoria: Categoria?, texto: String): Flow<List<Oferta>> = if (ofertaDao != null) {
        ofertaDao.filtrar(categoria, texto.trim()).map { lista -> lista.map { it.toDomain() } }
    } else {
        ofertas.map { lista ->
            lista.filter { oferta ->
                (categoria == null || oferta.categoria == categoria) &&
                    (
                        texto.isBlank() || oferta.nombre.contains(
                            texto.trim(),
                            ignoreCase = true
                        ) ||
                            oferta.comercio.contains(texto.trim(), ignoreCase = true)
                        )
            }
        }
    }
}
