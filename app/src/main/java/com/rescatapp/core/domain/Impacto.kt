package com.rescatapp.core.domain

data class Impacto(
    val rescates: Int = 0,
    val ahorrado: Double = 0.0,
    val kgAprovechados: Double = 0.0
) {
    val aprovechado: Double get() = kgAprovechados
    val reservas: Int get() = rescates
    val ahorro: Double get() = ahorrado
    val pesoKg: Double get() = kgAprovechados
}
