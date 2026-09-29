package com.rescatapp.core.data

fun generarSiguienteId(idsExistentes: List<Int>): Int = (idsExistentes.maxOrNull() ?: 0) + 1
