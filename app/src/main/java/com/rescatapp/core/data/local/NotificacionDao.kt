package com.rescatapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rescatapp.core.model.RolUsuario
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificacionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(notificacion: NotificacionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(notificaciones: List<NotificacionEntity>)

    @Query("SELECT * FROM notificaciones WHERE rolDestino = :rol ORDER BY fechaHora DESC")
    fun observarPorRol(rol: RolUsuario): Flow<List<NotificacionEntity>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE rolDestino = :rol AND leida = 0")
    fun contarNoLeidas(rol: RolUsuario): Flow<Int>

    @Query("UPDATE notificaciones SET leida = 1 WHERE id = :id")
    suspend fun marcarComoLeida(id: String)

    @Query("UPDATE notificaciones SET leida = 1 WHERE rolDestino = :rol")
    suspend fun marcarTodasComoLeidas(rol: RolUsuario)

    @Query("DELETE FROM notificaciones WHERE id = :id")
    suspend fun eliminarPorId(id: String): Int

    @Query("DELETE FROM notificaciones WHERE rolDestino = :rol")
    suspend fun eliminarTodasPorRol(rol: RolUsuario): Int
}
