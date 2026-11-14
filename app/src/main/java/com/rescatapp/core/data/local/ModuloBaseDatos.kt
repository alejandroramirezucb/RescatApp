package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rescatapp.core.data.ofertasDeEjemplo
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

    val MIGRACION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS notificaciones (
                    id TEXT NOT NULL PRIMARY KEY,
                    titulo TEXT NOT NULL,
                    mensaje TEXT NOT NULL,
                    fechaHora TEXT NOT NULL,
                    leida INTEGER NOT NULL,
                    tipo TEXT NOT NULL,
                    rolDestino TEXT NOT NULL,
                    pedidoId INTEGER,
                    ofertaId INTEGER
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notificaciones_rolDestino_leida ON notificaciones(rolDestino, leida)"
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS index_notificaciones_fechaHora ON notificaciones(fechaHora)"
            )
        }
    }

    @Provides
    @Singleton
    fun proveerRescatAppDatabase(
        @ApplicationContext contexto: Context,
        databaseProvider: Provider<RescatAppDatabase>
    ): RescatAppDatabase = Room.databaseBuilder(
        contexto,
        RescatAppDatabase::class.java,
        RescatAppDatabase.NOMBRE_BASE_DATOS
    ).addMigrations(MIGRACION_1_2)
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                CoroutineScope(Dispatchers.IO).launch {
                    val database = databaseProvider.get()
                    database.ofertaDao().insertarTodas(
                        ofertasDeEjemplo.map { it.toEntity() }
                    )
                    database.pedidoDao().insertarTodos(
                        pedidosDeEjemplo.map { it.toEntity() }
                    )
                }
            }
        })
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()

    @Provides
    fun proveerPedidoDao(database: RescatAppDatabase): PedidoDao = database.pedidoDao()

    @Provides
    fun proveerOfertaDao(database: RescatAppDatabase): OfertaDao = database.ofertaDao()

    @Provides
    fun proveerNotificacionDao(database: RescatAppDatabase): NotificacionDao =
        database.notificacionDao()
}
