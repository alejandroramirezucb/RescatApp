package com.rescatapp.features.registro.domain

import com.rescatapp.core.util.aDecimalOrNull
import com.rescatapp.core.util.aEnteroPositivoOrNull
import com.rescatapp.core.util.aHoraOrNull

class ValidarOfertaUseCase {
    operator fun invoke(campos: CamposRegistro): ErroresRegistro {
        val peso = campos.pesoKg.aDecimalOrNull()?.takeIf { it > 0 }
        val precioNormal = campos.precioNormal.aDecimalOrNull()?.takeIf { it > 0 }
        val precioRescate = campos.precioRescate.aDecimalOrNull()?.takeIf { it > 0 }
        val cantidad = campos.cantidadDisponible.aEnteroPositivoOrNull()
        val horaDesde = campos.horaRetiroDesde.aHoraOrNull()
        val horaHasta = campos.horaRetiroHasta.aHoraOrNull()

        return ErroresRegistro(
            nombre = if (campos.nombre.isBlank()) MensajeError.NOMBRE_VACIO else null,
            comercio = if (campos.comercio.isBlank()) MensajeError.COMERCIO_VACIO else null,
            categoria = if (campos.categoria == null) MensajeError.CATEGORIA_VACIA else null,
            descripcion = if (campos.descripcion.isBlank()) {
                MensajeError.DESCRIPCION_VACIA
            } else {
                null
            },
            pesoKg = if (peso == null) MensajeError.PESO_VACIO else null,
            precioNormal = if (precioNormal == null) MensajeError.PRECIO_NORMAL_VACIO else null,
            precioRescate = when {
                precioRescate == null -> MensajeError.PRECIO_RESCATE_VACIO

                precioNormal != null && precioRescate >= precioNormal ->
                    MensajeError.PRECIO_RESCATE_MENOR_NORMAL

                else -> null
            },
            cantidadDisponible = if (cantidad == null) MensajeError.CANTIDAD_INVALIDA else null,
            horaRetiroDesde = if (horaDesde == null) MensajeError.FORMATO_HORA_INVALIDO else null,
            horaRetiroHasta = when {
                horaHasta == null -> MensajeError.FORMATO_HORA_INVALIDO
                horaDesde != null && !horaHasta.isAfter(horaDesde) -> MensajeError.HORA_FINAL_MAYOR
                else -> null
            }
        )
    }
}
