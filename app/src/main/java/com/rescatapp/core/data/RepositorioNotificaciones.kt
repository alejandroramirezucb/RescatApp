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
class RepositorioNotificaciones @Inject constructor(
    private val notificacionDao: NotificacionDao,
    private val supabase: com.rescatapp.core.data.supabase.SupabaseConexion? = null
) {
    fun observarPorRol(rol: RolUsuario): Flow<List<Notificacion>> =
        supabase?.notificaciones?.map { lista -> lista.filter { it.rolDestino == rol } }
            ?: notificacionDao.observarPorRol(rol).map { lista ->
                lista.map { it.toDomain() }
            }

    fun contarNoLeidas(rol: RolUsuario): Flow<Int> = if (supabase !=
        null
    ) {
        observarPorRol(rol).map { lista -> lista.count { !it.leida } }
    } else {
        notificacionDao.contarNoLeidas(rol)
    }

    suspend fun agregar(notificacion: Notificacion) {
        if (supabase != null) return // Las notificaciones privadas las crea el servidor.
        notificacionDao.insertar(notificacion.toEntity())
    }

    suspend fun marcarComoLeida(id: String) {
        if (supabase !=
            null
        ) {
            return supabase.modificarNotificaciones(id, todas = false, eliminar = false)
        }
        notificacionDao.marcarComoLeida(id)
    }

    suspend fun marcarTodasComoLeidas(rol: RolUsuario) {
        if (supabase !=
            null
        ) {
            return supabase.modificarNotificaciones(null, todas = true, eliminar = false)
        }
        notificacionDao.marcarTodasComoLeidas(rol)
    }

    suspend fun eliminar(id: String) {
        if (supabase !=
            null
        ) {
            return supabase.modificarNotificaciones(id, todas = false, eliminar = true)
        }
        notificacionDao.eliminarPorId(id)
    }

    suspend fun eliminarTodas(rol: RolUsuario) {
        if (supabase !=
            null
        ) {
            return supabase.modificarNotificaciones(null, todas = true, eliminar = true)
        }
        notificacionDao.eliminarTodasPorRol(rol)
    }
}
