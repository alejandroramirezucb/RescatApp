package com.rescatapp.core.domain

import com.rescatapp.core.util.aDecimalPositivoOrNull
import com.rescatapp.core.util.aHoraOrNull
import javax.inject.Inject

data class ErroresCamposOferta(
    val cantidadDisponible: String? = null,
    val precioRescate: String? = null,
    val horaRetiroDesde: String? = null,
    val horaRetiroHasta: String? = null
) {
    val hayErrores: Boolean
        get() = listOfNotNull(cantidadDisponible, precioRescate, horaRetiroDesde, horaRetiroHasta)
            .isNotEmpty()
}

class ValidadorCamposOferta @Inject constructor() {
    operator fun invoke(
        cantidad: String,
        precioRescate: String,
        precioNormal: Double?,
        horaDesde: String,
        horaHasta: String,
        permitirCantidadCero: Boolean = false
    ): ErroresCamposOferta {
        val unidades = cantidad.trim().toIntOrNull()
        val precio = precioRescate.aDecimalPositivoOrNull()
        val desde = horaDesde.aHoraOrNull()
        val hasta = horaHasta.aHoraOrNull()
        val cantidadMinima = if (permitirCantidadCero) 0 else 1
        return ErroresCamposOferta(
            cantidadDisponible = if (unidades == null || unidades < cantidadMinima) {
                if (permitirCantidadCero) CANTIDAD_EDICION_INVALIDA else CANTIDAD_INVALIDA
            } else {
                null
            },
            precioRescate = when {
                precio == null -> PRECIO_RESCATE_INVALIDO
                precioNormal != null && precio >= precioNormal -> PRECIO_RESCATE_NO_MENOR
                else -> null
            },
            horaRetiroDesde = FORMATO_HORA_INVALIDO.takeIf { desde == null },
            horaRetiroHasta = when {
                hasta == null -> FORMATO_HORA_INVALIDO
                desde != null && !hasta.isAfter(desde) -> HORA_FINAL_NO_POSTERIOR
                else -> null
            }
        )
    }

    companion object {
        const val CANTIDAD_INVALIDA = "Ingresa una cantidad válida"
        const val CANTIDAD_EDICION_INVALIDA = "Ingresa una cantidad entera mayor o igual a 0"
        const val PRECIO_RESCATE_INVALIDO = "Ingresa un precio de rescate válido"
        const val PRECIO_RESCATE_NO_MENOR = "Debe ser menor al precio normal"
        const val FORMATO_HORA_INVALIDO = "Usa el formato HH:mm"
        const val HORA_FINAL_NO_POSTERIOR = "Debe ser posterior a la hora de inicio"
    }
}
