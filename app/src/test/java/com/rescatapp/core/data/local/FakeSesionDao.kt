package com.rescatapp.core.data.local

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSesionDao(sesionInicial: SesionEntity? = null) : SesionDao {

    private val sesion = MutableStateFlow(sesionInicial)

    override suspend fun guardarSesion(sesion: SesionEntity) {
        this.sesion.value = sesion
    }

    override fun observarSesion(): Flow<SesionEntity?> = sesion.asStateFlow()

    override suspend fun obtenerSesion(): SesionEntity? = sesion.value

    override suspend fun limpiarSesion(): Int {
        val habiaSesion = sesion.value != null
        sesion.value = null
        return if (habiaSesion) 1 else 0
    }
}
