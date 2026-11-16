package com.rescatapp.features.inicio.ui

import com.rescatapp.core.data.remote.FuenteOfertasRemotas
import com.rescatapp.core.model.EstadoRemoto
import com.rescatapp.core.model.Oferta

internal class FuenteRemotaFalsa(val ofertas: MutableList<Oferta>) : FuenteOfertasRemotas {
    var consultar: (suspend () -> List<Oferta>)? = null
    var antesDeMutar: suspend () -> Unit = {}
    var consultas = 0
    var mutaciones = 0
    var ultimoId: Int? = null

    override suspend fun obtenerOfertasRemotas(): List<Oferta> {
        consultas++
        return consultar?.invoke() ?: ofertas.toList()
    }

    override suspend fun crearOferta(oferta: Oferta): Oferta {
        mutaciones++
        antesDeMutar()
        return oferta.copy(id = 999).also { ofertas.add(it) }
    }

    override suspend fun editarOferta(id: Int, ofertaActualizada: Oferta): Oferta {
        mutaciones++
        ultimoId = id
        antesDeMutar()
        ofertas.replaceAll { if (it.id == id) ofertaActualizada else it }
        return ofertaActualizada
    }

    override suspend fun eliminarOferta(id: Int) {
        mutaciones++
        ultimoId = id
        antesDeMutar()
        ofertas.removeAll { it.id == id }
    }
}

internal fun ofertasEnLinea(vm: OfertasNegocioViewModel): List<Oferta> =
    (vm.uiState.value.estadoRemoto as EstadoRemoto.Exito).datos
