package com.rescatapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SesionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarSesion(sesion: SesionEntity)

    @Query("SELECT * FROM sesion_activa WHERE id = 1 LIMIT 1")
    fun observarSesion(): Flow<SesionEntity?>

    @Query("SELECT * FROM sesion_activa WHERE id = 1 LIMIT 1")
    suspend fun obtenerSesion(): SesionEntity?

    @Query("DELETE FROM sesion_activa")
    suspend fun limpiarSesion(): Int
}
