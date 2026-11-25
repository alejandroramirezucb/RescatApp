package com.rescatapp.core.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRACION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE pedidos ADD COLUMN clienteNombre TEXT")
        db.execSQL("ALTER TABLE pedidos ADD COLUMN clienteTelefono TEXT")
    }
}

val MIGRACION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE pedidos ADD COLUMN estadoPagoSimulado TEXT NOT NULL DEFAULT 'APROBADO'"
        )
        db.execSQL("UPDATE pedidos SET estadoPagoSimulado = 'ANULADO' WHERE estado = 'CANCELADO'")
    }
}

val MIGRACION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE ofertas ADD COLUMN latitud REAL")
        db.execSQL("ALTER TABLE ofertas ADD COLUMN longitud REAL")
    }
}
