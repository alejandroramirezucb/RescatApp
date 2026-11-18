package com.rescatapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OfertaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(ofertas: List<OfertaEntity>)

    @Query("DELETE FROM ofertas WHERE id NOT IN (:ids)")
    suspend fun eliminarExcepto(ids: List<Int>)

    @Query("SELECT * FROM ofertas ORDER BY id DESC")
    fun observarTodas(): Flow<List<OfertaEntity>>

    @Query(
        """
        SELECT * FROM ofertas
        WHERE estadoPublicacion = 'ACTIVA' AND cantidadDisponible > 0
          AND (:categoria IS NULL OR categoria = :categoria)
          AND (nombre LIKE '%' || :texto || '%' OR comercio LIKE '%' || :texto || '%')
        ORDER BY id DESC
        """
    )
    fun filtrar(categoria: String?, texto: String): Flow<List<OfertaEntity>>
}
