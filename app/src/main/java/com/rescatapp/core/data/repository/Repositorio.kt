package com.rescatapp.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Repositorio<T> {
    val elementos: StateFlow<List<T>>
    fun obtenerPorId(id: Int): Flow<T?>
    fun agregar(elemento: T): T
    fun actualizar(elemento: T)
}
