package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rescatapp.core.data.pedidosDeEjemplo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Module
@InstallIn(SingletonComponent::class)
object ModuloBaseDatos {

    @Provides
    @Singleton
    fun proveerRescatAppDatabase(
        @ApplicationContext contexto: Context,
        databaseProvider: Provider<RescatAppDatabase>
    ): RescatAppDatabase = Room.databaseBuilder(
        contexto,
        RescatAppDatabase::class.java,
        RescatAppDatabase.NOMBRE_BASE_DATOS
    ).addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                databaseProvider.get().pedidoDao().insertarTodos(
                    pedidosDeEjemplo.map { it.toEntity() }
                )
            }
        }
    }).build()

    @Provides
    fun proveerPedidoDao(database: RescatAppDatabase): PedidoDao = database.pedidoDao()
}
