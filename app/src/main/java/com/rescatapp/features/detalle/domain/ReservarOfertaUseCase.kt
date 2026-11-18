package com.rescatapp.features.detalle.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.data.RepositorioPedidos
import com.rescatapp.core.model.ResultadoOperacion
import javax.inject.Inject

class ReservarOfertaUseCase @Inject constructor(
    private val repositorioOfertas: RepositorioOfertas,
    private val repositorioPedidos: RepositorioPedidos
) {
    suspend operator fun invoke(ofertaId: Int): ResultadoOperacion {
        val resultado = repositorioPedidos.reservar(ofertaId)
        runCatching { repositorioOfertas.sincronizar() }
        return resultado
    }
}
