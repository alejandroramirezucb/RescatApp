package com.rescatapp.core.data.local

import androidx.room.TypeConverter
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido

class RescatAppTypeConverters {

    @TypeConverter
    fun fromEstadoPedido(estado: EstadoPedido): String = estado.name

    @TypeConverter
    fun toEstadoPedido(valor: String): EstadoPedido =
        runCatching { EstadoPedido.valueOf(valor) }.getOrDefault(EstadoPedido.RESERVADO)

    @TypeConverter
    fun fromCategoria(categoria: Categoria): String = categoria.name

    @TypeConverter
    fun toCategoria(valor: String): Categoria =
        runCatching { Categoria.valueOf(valor) }.getOrDefault(Categoria.COMIDA)
}
