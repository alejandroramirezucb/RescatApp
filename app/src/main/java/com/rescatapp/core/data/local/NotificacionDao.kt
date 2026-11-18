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
    suspend fun insertarTodas(notificaciones: List<NotificacionEntity>)

    @Query("DELETE FROM notificaciones WHERE id NOT IN (:ids)")
    suspend fun eliminarExcepto(ids: List<String>)

    @Query("SELECT * FROM notificaciones WHERE rolDestino = :rol ORDER BY fechaHora DESC")
    fun observarPorRol(rol: RolUsuario): Flow<List<NotificacionEntity>>

    @Query("SELECT COUNT(*) FROM notificaciones WHERE rolDestino = :rol AND leida = 0")
    fun contarNoLeidas(rol: RolUsuario): Flow<Int>
}
