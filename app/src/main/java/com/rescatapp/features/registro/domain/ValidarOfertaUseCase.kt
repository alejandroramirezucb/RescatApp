package com.rescatapp.features.registro.domain

import com.rescatapp.core.domain.ValidadorCamposOferta
import com.rescatapp.core.util.aDecimalPositivoOrNull
import javax.inject.Inject

class ValidarOfertaUseCase @Inject constructor(private val validarCampos: ValidadorCamposOferta) {
    constructor() : this(ValidadorCamposOferta())

    operator fun invoke(campos: CamposRegistro): ErroresRegistro {
        val precioNormal = campos.precioNormal.aDecimalPositivoOrNull()
        val erroresCampos = validarCampos(
            cantidad = campos.cantidadDisponible,
            precioRescate = campos.precioRescate,
            precioNormal = precioNormal,
            horaDesde = campos.horaRetiroDesde,
            horaHasta = campos.horaRetiroHasta
        )
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
            precioRescate = erroresCampos.precioRescate,
            cantidadDisponible = erroresCampos.cantidadDisponible,
            horaRetiroDesde = erroresCampos.horaRetiroDesde,
            horaRetiroHasta = erroresCampos.horaRetiroHasta
        )
    }
}
