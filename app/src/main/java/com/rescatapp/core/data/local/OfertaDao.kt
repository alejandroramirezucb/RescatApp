package com.rescatapp.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rescatapp.core.model.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface OfertaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(oferta: OfertaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodas(ofertas: List<OfertaEntity>)

    @Update
    suspend fun actualizar(oferta: OfertaEntity)

    @Delete
    suspend fun eliminar(oferta: OfertaEntity)

    @Query("DELETE FROM ofertas WHERE id = :id")
    suspend fun eliminarPorId(id: Int): Int

    @Query("SELECT * FROM ofertas ORDER BY id DESC")
    fun observarTodas(): Flow<List<OfertaEntity>>

    @Query("SELECT * FROM ofertas WHERE id = :id")
    fun observarPorId(id: Int): Flow<OfertaEntity?>

    @Query("SELECT * FROM ofertas WHERE id = :id")
    suspend fun buscarPorId(id: Int): OfertaEntity?

    @Query(
        """
        SELECT * FROM ofertas 
        WHERE (:categoria IS NULL OR categoria = :categoria) 
          AND (:texto IS NULL OR :texto = '' OR nombre LIKE '%' || :texto || '%') 
        ORDER BY id DESC
        """
    )
    fun filtrar(categoria: String?, texto: String?): Flow<List<OfertaEntity>>
}

fun OfertaDao.filtrar(categoria: Categoria?, texto: String?): Flow<List<OfertaEntity>> =
    filtrar(categoria?.name, texto)
