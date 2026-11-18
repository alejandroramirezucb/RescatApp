package com.rescatapp.core.data

import com.rescatapp.core.data.local.NotificacionDao
import com.rescatapp.core.data.local.toEntity
import com.rescatapp.core.data.remote.SupabaseApi
import com.rescatapp.core.model.Notificacion
import com.rescatapp.core.model.RolUsuario
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RepositorioNotificaciones @Inject constructor(
    private val api: SupabaseApi,
    private val notificacionDao: NotificacionDao,
    private val autenticacion: RepositorioAutenticacion
) {
    private var ultimaRevision = LocalDateTime.now()

    fun observarPorRol(rol: RolUsuario): Flow<List<Notificacion>> =
        notificacionDao.observarPorRol(rol).map { lista -> lista.map { it.toDomain() } }

    fun contarNoLeidas(rol: RolUsuario): Flow<Int> = notificacionDao.contarNoLeidas(rol)

    suspend fun sincronizar(): List<Notificacion> {
        val token = autenticacion.token()
        val remotas = if (token == null) {
            emptyList()
        } else {
            api.obtenerNotificaciones(token).map { it.toDomain() }
        }
        notificacionDao.insertarTodas(remotas.map { it.toEntity() })
        notificacionDao.eliminarExcepto(remotas.map { it.id })
        val nuevas = remotas.filter { !it.leida && it.fechaHora.isAfter(ultimaRevision) }
        ultimaRevision = LocalDateTime.now()
        return nuevas
    }

    suspend fun marcarComoLeida(id: String) = operar { token, usuarioId ->
        api.marcarNotificacionesLeidas(token, "eq.$usuarioId", "eq.$id")
    }

    suspend fun marcarTodasComoLeidas() = operar { token, usuarioId ->
        api.marcarNotificacionesLeidas(token, "eq.$usuarioId", null)
    }

    suspend fun eliminar(id: String) = operar { token, usuarioId ->
        api.eliminarNotificaciones(token, "eq.$usuarioId", "eq.$id")
    }

    suspend fun eliminarTodas() = operar { token, usuarioId ->
        api.eliminarNotificaciones(token, "eq.$usuarioId", null)
    }

    private suspend fun operar(accion: suspend (token: String, usuarioId: String) -> Unit) {
        val token = autenticacion.tokenObligatorio()
        accion(token, checkNotNull(autenticacion.sesion()).usuarioId)
        runCatching { sincronizar() }
    }
}
