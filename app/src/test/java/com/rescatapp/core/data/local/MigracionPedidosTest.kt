package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.core.data.pedidosDeEjemplo
import com.rescatapp.core.model.EstadoPagoSimulado
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], application = android.app.Application::class)
class MigracionPedidosTest {
    @Test
    fun migracionDesdeVersion5ConservaPedidosYSesionYPermiteGuardarElComprador() = runTest {
        verificarMigracion(5)
    }

    @Test
    fun migracionDesdeVersion6ConservaContactoYSesionYMigraElPagoDeLosCancelados() = runTest {
        verificarMigracion(6)
    }

    private suspend fun verificarMigracion(versionInicial: Int) {
        val contexto = ApplicationProvider.getApplicationContext<Context>()
        val nombre = "migracion-pedidos.db"
        contexto.deleteDatabase(nombre)
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(contexto)
                .name(nombre)
                .callback(object : SupportSQLiteOpenHelper.Callback(versionInicial) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        val esquema = checkNotNull(javaClass.getResourceAsStream("/room_v5.sql"))
                            .bufferedReader().use { it.readLines() }
                        esquema.filter { it.isNotBlank() }.forEach { db.execSQL(it) }
                        if (versionInicial == 6) MIGRACION_5_6.migrate(db)
                        db.execSQL(
                            """INSERT INTO pedidos
                                (id, ofertaId, nombreOferta, comercio, categoria, precioPagado,
                                ahorro, pesoKg, horaRetiroDesde, horaRetiroHasta, estado,
                                fecha, fechaRetiro)
                                VALUES (77, 1, 'Pack', 'Panadería', 'PANADERIA', 25, 20, 1,
                                '18:00', '20:00', 'RESERVADO', '2026-11-24', '2026-11-25')"""
                        )
                        db.execSQL(
                            """INSERT INTO pedidos
                                (id, ofertaId, nombreOferta, comercio, categoria, precioPagado,
                                ahorro, pesoKg, horaRetiroDesde, horaRetiroHasta, estado,
                                fecha, fechaRetiro)
                                SELECT 78, ofertaId, nombreOferta, comercio, categoria, precioPagado,
                                ahorro, pesoKg, horaRetiroDesde, horaRetiroHasta, 'CANCELADO',
                                fecha, fechaRetiro FROM pedidos WHERE id = 77"""
                        )
                        if (versionInicial == 6) {
                            db.execSQL(
                                "UPDATE pedidos SET clienteNombre = 'Ana', clienteTelefono = '71234567'"
                            )
                        }
                        db.execSQL(
                            """INSERT INTO sesion_activa
                                (id, usuarioId, correo, nombre, rol, direccion, token,
                                refreshToken, expira)
                                VALUES (1, 'usuario', 'ana@correo.com', 'Ana', 'CLIENTE', '',
                                'acceso', 'renovacion', 2000000000)"""
                        )
                    }

                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) = Unit
                }).build()
        )
        helper.writableDatabase
        helper.close()

        val database = Room.databaseBuilder(contexto, RescatAppDatabase::class.java, nombre)
            .addMigrations(MIGRACION_5_6, MIGRACION_6_7, MIGRACION_7_8)
            .allowMainThreadQueries()
            .build()
        try {
            val antiguo = checkNotNull(database.pedidoDao().buscarPorId(77))
            assertEquals("Pack", antiguo.nombreOferta)
            if (versionInicial == 5) {
                assertNull(antiguo.clienteNombre)
                assertNull(antiguo.clienteTelefono)
            } else {
                assertEquals("Ana", antiguo.clienteNombre)
                assertEquals("71234567", antiguo.clienteTelefono)
            }
            assertEquals(EstadoPagoSimulado.APROBADO, antiguo.toDomain().estadoPagoSimulado)
            assertEquals(
                EstadoPagoSimulado.ANULADO,
                database.pedidoDao().buscarPorId(78)?.toDomain()?.estadoPagoSimulado
            )
            assertEquals("acceso", database.sesionDao().obtenerSesion()?.token)
            val nuevo = pedidosDeEjemplo.first().copy(
                clienteNombre = "Ana Pérez",
                clienteTelefono = "71234567"
            )
            database.pedidoDao().insertarTodos(listOf(nuevo.toEntity()))
            assertEquals(nuevo, database.pedidoDao().buscarPorId(nuevo.id)?.toDomain())
        } finally {
            database.close()
            contexto.deleteDatabase(nombre)
        }
    }
}
