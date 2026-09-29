package com.rescatapp.features.registro.domain

import com.rescatapp.core.util.aDecimalPositivoOrNull
import com.rescatapp.core.util.aEnteroPositivoOrNull
import com.rescatapp.core.util.aHoraOrNull
import java.time.LocalTime
import javax.inject.Inject

class ValidarOfertaUseCase @Inject constructor() {
    operator fun invoke(campos: CamposRegistro): ErroresRegistro {
        val precioNormal = campos.precioNormal.aDecimalPositivoOrNull()
        val horaDesde = campos.horaRetiroDesde.aHoraOrNull()
        return ErroresRegistro(
            nombre = MensajesValidacion.NOMBRE_VACIO
                .takeIf { campos.nombre.isBlank() },
            comercio = MensajesValidacion.COMERCIO_VACIO
                .takeIf { campos.comercio.isBlank() },
            categoria = MensajesValidacion.CATEGORIA_SIN_ELEGIR
                .takeIf { campos.categoria == null },
            descripcion = MensajesValidacion.DESCRIPCION_VACIA
                .takeIf { campos.descripcion.isBlank() },
            pesoKg = MensajesValidacion.PESO_INVALIDO
                .takeIf { campos.pesoKg.aDecimalPositivoOrNull() == null },
            precioNormal = MensajesValidacion.PRECIO_NORMAL_INVALIDO
                .takeIf { precioNormal == null },
            precioRescate = validarPrecioRescate(campos.precioRescate, precioNormal),
            cantidadDisponible = MensajesValidacion.CANTIDAD_INVALIDA
                .takeIf { campos.cantidadDisponible.aEnteroPositivoOrNull() == null },
            horaRetiroDesde = MensajesValidacion.FORMATO_HORA_INVALIDO
                .takeIf { horaDesde == null },
            horaRetiroHasta = validarHoraHasta(campos.horaRetiroHasta, horaDesde)
        )
    }

    private fun validarPrecioRescate(texto: String, precioNormal: Double?): String? {
        val precioRescate = texto.aDecimalPositivoOrNull()
        return when {
            precioRescate == null -> MensajesValidacion.PRECIO_RESCATE_INVALIDO

            precioNormal != null && precioRescate >= precioNormal ->
                MensajesValidacion.PRECIO_RESCATE_NO_MENOR

            else -> null
        }
    }

    private fun validarHoraHasta(texto: String, horaDesde: LocalTime?): String? {
        val horaHasta = texto.aHoraOrNull()
        return when {
            horaHasta == null -> MensajesValidacion.FORMATO_HORA_INVALIDO

            horaDesde != null && !horaHasta.isAfter(horaDesde) ->
                MensajesValidacion.HORA_FINAL_NO_POSTERIOR

            else -> null
        }
    }
}
