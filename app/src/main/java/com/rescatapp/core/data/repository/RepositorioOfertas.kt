package com.rescatapp.core.data.repository

import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.StateFlow

@Singleton
class RepositorioOfertas @Inject constructor() : OfertaRepositoryEnMemoria() {
    val ofertas: StateFlow<List<Oferta>> get() = elementos

    fun obtenerPorId(id: Int): Oferta? = elementos.value.find { it.id == id }
}
