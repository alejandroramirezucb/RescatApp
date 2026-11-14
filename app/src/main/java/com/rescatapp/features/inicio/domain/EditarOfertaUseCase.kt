package com.rescatapp.features.inicio.domain

import com.rescatapp.core.data.RepositorioOfertas
import com.rescatapp.core.model.Oferta
import javax.inject.Inject

class EditarOfertaUseCase @Inject constructor(private val repositorioOfertas: RepositorioOfertas) {
    suspend operator fun invoke(oferta: Oferta) {
        repositorioOfertas.actualizar(oferta)
    }
}
