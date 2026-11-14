package com.rescatapp.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [PedidoEntity::class, OfertaEntity::class, NotificacionEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(RescatAppTypeConverters::class)
abstract class RescatAppDatabase : RoomDatabase() {

    abstract fun pedidoDao(): PedidoDao

    abstract fun ofertaDao(): OfertaDao

    abstract fun notificacionDao(): NotificacionDao

    companion object {
        const val NOMBRE_BASE_DATOS = "rescatapp.db"
    }
}
