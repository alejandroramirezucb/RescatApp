package com.rescatapp.core.data.local

import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNotificacionDao(notificacionesIniciales: List<Notificacion> = emptyList()) :
    NotificacionDao {

    private val entidades = MutableStateFlow(notificacionesIniciales.map { it.toEntity() })

    override suspend fun insertarTodas(notificaciones: List<NotificacionEntity>) {
        entidades.update { lista ->
            notificaciones + lista.filterNot { item -> notificaciones.any { it.id == item.id } }
        }
    }

    override suspend fun eliminarExcepto(ids: List<String>) {
        entidades.update { lista -> lista.filter { it.id in ids } }
    }

    override fun observarPorRol(rol: RolUsuario): Flow<List<NotificacionEntity>> =
        entidades.map { lista ->
            lista.filter { it.rolDestino == rol }.sortedByDescending { it.fechaHora }
        }

    override fun contarNoLeidas(rol: RolUsuario): Flow<Int> =
        entidades.map { lista -> lista.count { it.rolDestino == rol && !it.leida } }
}
