package com.rescatapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(usuarios: List<UsuarioEntity>)

    @Query("SELECT * FROM usuarios WHERE LOWER(correo) = LOWER(:correo) LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    suspend fun obtenerTodos(): List<UsuarioEntity>

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun eliminarPorId(id: String): Int

    @Query("DELETE FROM usuarios")
    suspend fun eliminarTodos(): Int
}
