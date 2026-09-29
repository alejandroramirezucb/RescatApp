package com.rescatapp.core.data

import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@Singleton
class RepositorioOfertas @Inject constructor() {
    private val ofertasMutables = MutableStateFlow(ofertasIniciales())
    val ofertas = ofertasMutables.asStateFlow()

    fun obtenerPorId(id: Int): Flow<Oferta?> = ofertas.map { lista ->
        lista.find { it.id == id }
    }

    fun obtenerActualPorId(id: Int): Oferta? = ofertas.value.find { it.id == id }

    fun agregar(oferta: Oferta): Oferta {
        val nuevoId = (ofertas.value.maxOfOrNull { it.id } ?: 0) + 1
        val nuevaOferta = oferta.copy(id = nuevoId)
        ofertasMutables.update { listOf(nuevaOferta) + it }
        return nuevaOferta
    }

    fun actualizar(oferta: Oferta) {
        ofertasMutables.update { lista ->
            lista.map { actual -> if (actual.id == oferta.id) oferta else actual }
        }
    }
}
