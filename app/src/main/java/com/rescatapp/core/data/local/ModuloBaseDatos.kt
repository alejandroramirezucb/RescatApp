package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rescatapp.core.data.ofertasDeEjemplo
import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
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

    val MIGRACION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id TEXT NOT NULL PRIMARY KEY,
                    nombre TEXT NOT NULL,
                    correo TEXT NOT NULL,
                    contrasenaHash TEXT NOT NULL,
                    rol TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    direccion TEXT NOT NULL,
                    nombreNegocio TEXT,
                    fechaCreacion INTEGER NOT NULL
                )
                """.trimIndent()
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_correo ON usuarios(correo)"
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS sesion_activa (
                    id INTEGER NOT NULL PRIMARY KEY,
                    usuarioId TEXT NOT NULL,
                    correo TEXT NOT NULL,
                    nombre TEXT NOT NULL,
                    rol TEXT NOT NULL,
                    nombreNegocio TEXT,
                    fechaInicio INTEGER NOT NULL,
                    token TEXT NOT NULL
                )
                """.trimIndent()
            )
            for (usuario in UsuariosDeEjemplo.todos) {
                val negocioSql = usuario.nombreNegocio?.let { "'$it'" } ?: "NULL"
                db.execSQL(
                    """
                    INSERT OR IGNORE INTO usuarios (id, nombre, correo, contrasenaHash, rol, telefono, direccion, nombreNegocio, fechaCreacion)
                    VALUES ('${usuario.id}', '${usuario.nombre}', '${usuario.correo}', '${usuario.contrasenaHash}', '${usuario.rol.name}', '${usuario.telefono}', '${usuario.direccion}', $negocioSql, ${usuario.fechaCreacion})
                    """.trimIndent()
                )
            }
        }
    }

    @Provides
    @Singleton
    fun proveerRescatAppDatabase(@ApplicationContext contexto: Context): RescatAppDatabase =
        Room.databaseBuilder(
            contexto,
            RescatAppDatabase::class.java,
            RescatAppDatabase.NOMBRE_BASE_DATOS
        ).addMigrations(MIGRACION_1_2, MIGRACION_2_3)
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
    fun proveerUsuarioDao(database: RescatAppDatabase): UsuarioDao = database.usuarioDao()

    @Provides
    fun proveerSesionDao(database: RescatAppDatabase): SesionDao = database.sesionDao()
}
