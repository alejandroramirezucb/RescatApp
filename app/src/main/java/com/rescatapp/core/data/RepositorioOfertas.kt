package com.rescatapp.core.data

import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@Singleton
class RepositorioOfertas(ofertasIniciales: List<Oferta>) {
    @Inject
    constructor() : this(ofertasDeEjemplo)

    private val ofertasEditables = MutableStateFlow(ofertasIniciales)
    val ofertas: StateFlow<List<Oferta>> = ofertasEditables.asStateFlow()

    fun obtenerPorId(id: Int): Flow<Oferta?> = ofertas.map { lista -> lista.find { it.id == id } }

    fun buscarPorId(id: Int): Oferta? = ofertas.value.find { it.id == id }

    fun agregar(oferta: Oferta): Oferta {
        val ofertaNueva = oferta.copy(id = generarSiguienteId(ofertas.value.map { it.id }))
        ofertasEditables.update { listOf(ofertaNueva) + it }
        return ofertaNueva
    }

    fun actualizar(oferta: Oferta) {
        ofertasEditables.update { lista -> lista.map { if (it.id == oferta.id) oferta else it } }
    }

    fun cambiarUnidadesDisponibles(ofertaId: Int, diferencia: Int) {
        val oferta = buscarPorId(ofertaId) ?: return
        actualizar(oferta.copy(cantidadDisponible = oferta.cantidadDisponible + diferencia))
    }
}
