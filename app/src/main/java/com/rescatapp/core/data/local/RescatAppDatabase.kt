package com.rescatapp.core.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.rescatapp.core.data.local.dao.OfertaDao
import com.rescatapp.core.data.local.entity.OfertaEntity
import com.rescatapp.core.model.Categoria
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [OfertaEntity::class], version = 1, exportSchema = false)
abstract class RescatAppDatabase : RoomDatabase() {

    abstract fun ofertaDao(): OfertaDao

    companion object {
        @Volatile
        private var INSTANCE: RescatAppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RescatAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RescatAppDatabase::class.java,
                    "rescatapp_database"
                )
                .addCallback(RescatAppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class RescatAppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.ofertaDao())
                }
            }
        }

        suspend fun populateDatabase(ofertaDao: OfertaDao) {
            // Pre-poblar con 14 ofertas basadas en el diseño
            val ofertasBase = listOf(
                OfertaEntity(1, "Pack Sorpresa", "Panadería La Central", Categoria.PANADERIA.name, "Surtido sorpresa", 0.5, 45.0, 25.0, 5, "18:00", "20:00", ""),
                OfertaEntity(2, "Pack Croissants", "Panadería La Central", Categoria.PANADERIA.name, "Croissants del día", 0.4, 30.0, 15.0, 3, "19:00", "21:00", ""),
                OfertaEntity(3, "Pizza Familiar", "Pizzería Don Marco", Categoria.COMIDA.name, "Pizza grande", 1.0, 80.0, 45.0, 2, "22:00", "23:30", ""),
                OfertaEntity(4, "2 Pizzas Medianas", "Pizzería Don Marco", Categoria.COMIDA.name, "Dos pizzas", 1.2, 110.0, 55.0, 1, "21:30", "23:00", ""),
                OfertaEntity(5, "Pack Café+", "Café Aroma", Categoria.CAFETERIA.name, "Bebida y postre", 0.3, 35.0, 18.0, 6, "17:00", "19:00", ""),
                OfertaEntity(6, "Pack Mañanero", "Café Aroma", Categoria.CAFETERIA.name, "Desayuno", 0.4, 40.0, 22.0, 4, "10:00", "12:00", ""),
                OfertaEntity(7, "Tortas Mix", "Dulcería Bella", Categoria.POSTRES.name, "Porciones varias", 0.6, 60.0, 30.0, 6, "20:00", "22:00", ""),
                OfertaEntity(8, "Caja Cupcakes", "Dulcería Bella", Categoria.POSTRES.name, "6 cupcakes", 0.5, 45.0, 25.0, 2, "19:30", "21:30", ""),
                OfertaEntity(9, "Bowl Saludable", "Green Kitchen", Categoria.COMIDA.name, "Ensalada", 0.4, 55.0, 28.0, 3, "14:00", "16:00", ""),
                OfertaEntity(10, "Pack Lácteos", "Súper Norte", Categoria.COMIDA.name, "Leche, yogur", 1.5, 50.0, 25.0, 8, "20:00", "22:00", ""),
                OfertaEntity(11, "Pack Empanadas", "Panadería La Central", Categoria.PANADERIA.name, "Empanadas mixtas", 0.5, 40.0, 20.0, 4, "21:00", "22:30", ""),
                OfertaEntity(12, "Burger + Papas", "Pizzería Don Marco", Categoria.COMIDA.name, "Hamburguesa clásica", 0.6, 65.0, 35.0, 2, "22:00", "23:45", ""),
                OfertaEntity(13, "Pack Frutas", "Green Kitchen", Categoria.COMIDA.name, "Frutas de temporada", 1.2, 45.0, 22.0, 5, "18:00", "20:00", ""),
                OfertaEntity(14, "Sándwich Combo", "Café Aroma", Categoria.CAFETERIA.name, "Sándwich y café", 0.4, 50.0, 28.0, 3, "18:30", "20:30", "")
            )
            ofertaDao.insertOfertas(ofertasBase)
        }
    }
}
