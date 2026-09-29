package com.rescatapp.core.data.repository

import com.rescatapp.core.data.mock.ofertasDeEjemplo
import com.rescatapp.core.model.Oferta
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class OfertaRepositoryEnMemoria @Inject constructor() : RepositorioEnMemoria<Oferta>(ofertasDeEjemplo) {
    override fun obtenerId(elemento: Oferta): Int = elemento.id
    override fun asignarId(elemento: Oferta, nuevoId: Int): Oferta = elemento.copy(id = nuevoId)
}
