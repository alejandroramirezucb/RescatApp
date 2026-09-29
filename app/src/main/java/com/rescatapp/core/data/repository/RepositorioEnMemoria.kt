package com.rescatapp.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

abstract class RepositorioEnMemoria<T>(
    datosIniciales: List<T> = emptyList()
) : Repositorio<T> {

    protected val _elementos = MutableStateFlow(datosIniciales)
    override val elementos: StateFlow<List<T>> = _elementos.asStateFlow()

    protected abstract fun obtenerId(elemento: T): Int
    protected abstract fun asignarId(elemento: T, nuevoId: Int): T

    override fun obtenerPorId(id: Int): Flow<T?> =
        elementos.map { lista -> lista.find { obtenerId(it) == id } }

    override fun agregar(elemento: T): T {
        var elementoResultado: T? = null
        _elementos.update { lista ->
            val nuevoId = (lista.maxOfOrNull { obtenerId(it) } ?: 0) + 1
            val nuevoElemento = asignarId(elemento, nuevoId)
            elementoResultado = nuevoElemento
            listOf(nuevoElemento) + lista
        }
        return elementoResultado!!
    }

    override fun actualizar(elemento: T) {
        _elementos.update { lista ->
            lista.map { actual ->
                if (obtenerId(actual) == obtenerId(elemento)) elemento else actual
            }
        }
    }
}
