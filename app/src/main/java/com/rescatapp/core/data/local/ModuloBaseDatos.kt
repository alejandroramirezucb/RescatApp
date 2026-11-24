package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuloBaseDatos {

    @Provides
    @Singleton
    fun proveerRescatAppDatabase(@ApplicationContext contexto: Context): RescatAppDatabase =
        Room.databaseBuilder(
            contexto,
            RescatAppDatabase::class.java,
            RescatAppDatabase.NOMBRE_BASE_DATOS
        ).addMigrations(MIGRACION_5_6)
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun proveerPedidoDao(database: RescatAppDatabase): PedidoDao = database.pedidoDao()

    @Provides
    fun proveerOfertaDao(database: RescatAppDatabase): OfertaDao = database.ofertaDao()

    @Provides
    fun proveerNotificacionDao(database: RescatAppDatabase): NotificacionDao =
        database.notificacionDao()

    @Provides
    fun proveerSesionDao(database: RescatAppDatabase): SesionDao = database.sesionDao()
}
