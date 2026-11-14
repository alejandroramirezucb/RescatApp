package com.rescatapp.core.data

import com.rescatapp.core.data.local.NotificacionDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RepositorioNotificaciones @Inject constructor(private val notificacionDao: NotificacionDao) {
    fun observarPorRol(rol: RolUsuario): Flow<List<Notificacion>> =
        notificacionDao.observarPorRol(rol).map { lista ->
            lista.map { it.toDomain() }
        }

    fun contarNoLeidas(rol: RolUsuario): Flow<Int> = notificacionDao.contarNoLeidas(rol)

    suspend fun agregar(notificacion: Notificacion) {
        notificacionDao.insertar(notificacion.toEntity())
    }

    suspend fun marcarComoLeida(id: String) {
        notificacionDao.marcarComoLeida(id)
    }

    suspend fun marcarTodasComoLeidas(rol: RolUsuario) {
        notificacionDao.marcarTodasComoLeidas(rol)
    }

    suspend fun eliminar(id: String): Boolean = notificacionDao.eliminarPorId(id) > 0

    suspend fun eliminarTodas(rol: RolUsuario): Boolean =
        notificacionDao.eliminarTodasPorRol(rol) > 0
}
