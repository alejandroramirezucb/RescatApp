package com.rescatapp.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rescatapp.core.data.local.entity.OfertaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OfertaDao {
    @Query("SELECT * FROM ofertas")
    fun getOfertasStream(): Flow<List<OfertaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOfertas(ofertas: List<OfertaEntity>)

    @Update
    suspend fun updateOferta(oferta: OfertaEntity)
    
    @Query("SELECT * FROM ofertas WHERE id = :ofertaId")
    suspend fun getOfertaById(ofertaId: Int): OfertaEntity?
}
