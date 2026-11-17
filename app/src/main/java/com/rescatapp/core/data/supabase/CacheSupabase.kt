package com.rescatapp.core.data.supabase

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Entity(tableName = "cache_cuentas", primaryKeys = ["cuenta", "recurso"])
data class EntradaCache(val cuenta: String, val recurso: String, val json: String)

@Dao
interface CacheCuentaDao {
    @Query("SELECT json FROM cache_cuentas WHERE cuenta = :cuenta AND recurso = :recurso")
    suspend fun leer(cuenta: String, recurso: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(entrada: EntradaCache)

    @Query("DELETE FROM cache_cuentas WHERE cuenta = :cuenta")
    suspend fun borrar(cuenta: String)
}

@Database(entities = [EntradaCache::class], version = 1, exportSchema = false)
abstract class SupabaseCacheDatabase : RoomDatabase() {
    abstract fun entradas(): CacheCuentaDao
}

interface CacheDeCuenta {
    suspend fun leer(cuenta: String, recurso: String): String?
    suspend fun guardar(cuenta: String, recurso: String, json: String)
    suspend fun borrar(cuenta: String)
}

@Singleton
class CacheSupabase @Inject constructor(@ApplicationContext context: Context) : CacheDeCuenta {
    // Base nueva: los registros demo anteriores no se atribuyen a cuentas reales.
    private val db = Room.databaseBuilder(
        context,
        SupabaseCacheDatabase::class.java,
        "supabase_cache.db"
    ).build()
    override suspend fun leer(cuenta: String, recurso: String): String? =
        db.entradas().leer(cuenta, recurso)
    override suspend fun guardar(cuenta: String, recurso: String, json: String) =
        db.entradas().guardar(EntradaCache(cuenta, recurso, json))
    override suspend fun borrar(cuenta: String) = db.entradas().borrar(cuenta)
}
