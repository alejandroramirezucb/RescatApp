package com.rescatapp.core.data.local

import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNotificacionDao(notificacionesIniciales: List<Notificacion> = emptyList()) :
    NotificacionDao {

    private val entidades = MutableStateFlow(
        notificacionesIniciales.map { it.toEntity() }.sortedByDescending { it.fechaHora }
    )

    override suspend fun insertar(notificacion: NotificacionEntity) {
        entidades.update { lista ->
            (listOf(notificacion) + lista.filterNot { it.id == notificacion.id })
                .sortedByDescending { it.fechaHora }
        }
    }

    override suspend fun insertarTodas(notificaciones: List<NotificacionEntity>) {
        entidades.update { lista ->
            (notificaciones + lista.filterNot { item -> notificaciones.any { it.id == item.id } })
                .sortedByDescending { it.fechaHora }
        }
    }

    override fun observarPorRol(rol: RolUsuario): Flow<List<NotificacionEntity>> =
        entidades.map { lista -> lista.filter { it.rolDestino == rol } }

    override fun contarNoLeidas(rol: RolUsuario): Flow<Int> =
        entidades.map { lista -> lista.count { it.rolDestino == rol && !it.leida } }

    override suspend fun marcarComoLeida(id: String) {
        entidades.update { lista ->
            lista.map { if (it.id == id) it.copy(leida = true) else it }
        }
    }

    override suspend fun marcarTodasComoLeidas(rol: RolUsuario) {
        entidades.update { lista ->
            lista.map { if (it.rolDestino == rol) it.copy(leida = true) else it }
        }
    }

    override suspend fun eliminarPorId(id: String): Int {
        val antes = entidades.value.size
        entidades.update { lista -> lista.filterNot { it.id == id } }
        return antes - entidades.value.size
    }

    override suspend fun eliminarTodasPorRol(rol: RolUsuario): Int {
        val antes = entidades.value.size
        entidades.update { lista -> lista.filterNot { it.rolDestino == rol } }
        return antes - entidades.value.size
    }
}
