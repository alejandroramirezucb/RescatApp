package com.rescatapp.core.data.local

import androidx.room.TypeConverter
import com.rescatapp.core.model.Categoria
import com.rescatapp.core.model.EstadoPedido
import com.rescatapp.core.model.RolUsuario
import com.rescatapp.core.model.TipoNotificacion
import java.time.LocalDate
import java.time.LocalDateTime

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

    @TypeConverter
    fun fromLocalDate(fecha: LocalDate): String = fecha.toString()

    @TypeConverter
    fun toLocalDate(valor: String): LocalDate =
        runCatching { LocalDate.parse(valor) }.getOrDefault(LocalDate.now())

    @TypeConverter
    fun fromTipoNotificacion(tipo: TipoNotificacion): String = tipo.name

    @TypeConverter
    fun toTipoNotificacion(valor: String): TipoNotificacion =
        runCatching { TipoNotificacion.valueOf(valor) }.getOrDefault(TipoNotificacion.SISTEMA)

    @TypeConverter
    fun fromRolUsuario(rol: RolUsuario): String = rol.name

    @TypeConverter
    fun toRolUsuario(valor: String): RolUsuario =
        runCatching { RolUsuario.valueOf(valor) }.getOrDefault(RolUsuario.CLIENTE)

    @TypeConverter
    fun fromLocalDateTime(fechaHora: LocalDateTime): String = fechaHora.toString()

    @TypeConverter
    fun toLocalDateTime(valor: String): LocalDateTime =
        runCatching { LocalDateTime.parse(valor) }.getOrDefault(LocalDateTime.now())
}
