package com.rescatapp.features.auth.domain

import com.rescatapp.core.data.RepositorioAutenticacion
import javax.inject.Inject

class CerrarSesionUseCase @Inject constructor(
    private val repositorioAutenticacion: RepositorioAutenticacion
) {
    suspend operator fun invoke() {
        repositorioAutenticacion.cerrarSesion()
    }
}
