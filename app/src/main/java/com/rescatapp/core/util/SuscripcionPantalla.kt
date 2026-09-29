package com.rescatapp.core.util

import kotlinx.coroutines.flow.SharingStarted

private const val ESPERA_AL_SALIR_DE_PANTALLA_MS = 5_000L

val suscripcionPantalla: SharingStarted = SharingStarted.WhileSubscribed(
    ESPERA_AL_SALIR_DE_PANTALLA_MS
)
