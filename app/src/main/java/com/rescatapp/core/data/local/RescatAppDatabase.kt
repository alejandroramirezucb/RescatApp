package com.rescatapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PedidoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RescatAppTypeConverters::class)
abstract class RescatAppDatabase : RoomDatabase() {

    abstract fun pedidoDao(): PedidoDao

    companion object {
        const val NOMBRE_BASE_DATOS = "rescatapp.db"
    }
}
