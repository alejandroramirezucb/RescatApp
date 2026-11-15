package com.rescatapp.core.data.local

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import com.rescatapp.features.auth.data.UsuariosDeEjemplo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MigracionBaseDatosTest {

    @Test
    fun migracion2a3CreaTablasYPueblaUsuariosDemo() {
        val contexto: Context = ApplicationProvider.getApplicationContext()
        val config = SupportSQLiteOpenHelper.Configuration.builder(contexto)
            .name(null)
            .callback(
                object : SupportSQLiteOpenHelper.Callback(2) {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        ModuloBaseDatos.MIGRACION_1_2.migrate(db)
                    }

                    override fun onUpgrade(
                        db: SupportSQLiteDatabase,
                        oldVersion: Int,
                        newVersion: Int
                    ) {
                        if (oldVersion < 3) {
                            ModuloBaseDatos.MIGRACION_2_3.migrate(db)
                        }
                    }
                }
            )
            .build()

        val helper = FrameworkSQLiteOpenHelperFactory().create(config)
        val db = helper.writableDatabase

        ModuloBaseDatos.MIGRACION_2_3.migrate(db)

        val cursorUsuarios = db.query("SELECT COUNT(*) FROM usuarios")
        cursorUsuarios.moveToFirst()
        val cantidadUsuarios = cursorUsuarios.getInt(0)
        cursorUsuarios.close()
        assertEquals(UsuariosDeEjemplo.todos.size, cantidadUsuarios)

        val cursorSesion = db.query("SELECT COUNT(*) FROM sesion_activa")
        cursorSesion.moveToFirst()
        val cantidadSesiones = cursorSesion.getInt(0)
        cursorSesion.close()
        assertEquals(0, cantidadSesiones)

        val cursorDemo = db.query(
            "SELECT correo FROM usuarios WHERE correo = 'invitado@example.com'"
        )
        assertTrue(cursorDemo.moveToFirst())
        cursorDemo.close()

        db.close()
    }
}
