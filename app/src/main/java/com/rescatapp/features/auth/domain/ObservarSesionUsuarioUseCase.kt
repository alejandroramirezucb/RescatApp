package com.rescatapp.features.auth.domain

import com.rescatapp.core.data.RepositorioAutenticacion
import com.rescatapp.features.auth.domain.model.SesionUsuario
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarSesionUsuarioUseCase @Inject constructor(
    private val repositorioAutenticacion: RepositorioAutenticacion
) {
    operator fun invoke(): Flow<SesionUsuario?> = repositorioAutenticacion.sesionActual
}
