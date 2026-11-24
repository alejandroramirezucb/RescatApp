package com.rescatapp.core.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRACION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE pedidos ADD COLUMN clienteNombre TEXT")
        db.execSQL("ALTER TABLE pedidos ADD COLUMN clienteTelefono TEXT")
    }
}
