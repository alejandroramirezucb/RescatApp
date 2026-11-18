package com.rescatapp.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(pedidos: List<PedidoEntity>)

    @Query("DELETE FROM pedidos WHERE id NOT IN (:ids)")
    suspend fun eliminarExcepto(ids: List<Int>)

    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun observarTodos(): Flow<List<PedidoEntity>>

    @Query("SELECT * FROM pedidos WHERE id = :id")
    suspend fun buscarPorId(id: Int): PedidoEntity?
}
